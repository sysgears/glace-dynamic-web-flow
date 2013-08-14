package com.sysgears.gdwf.execution.route

import com.sysgears.gdwf.FlowStage
import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.dispatch.EventDispatcher
import com.sysgears.gdwf.dispatch.FlowAction
import com.sysgears.gdwf.exceptions.FlowValidationException
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.persistence.FlowExecutionListeners
import com.sysgears.gdwf.registry.FlowItem
import com.sysgears.gdwf.registry.FlowRegistry
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.repository.EntryValidator
import com.sysgears.gdwf.repository.FlowSnapshotHolder
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.token.TokenGenerator
import com.sysgears.gdwf.token.TokenProxy
import org.springframework.web.context.request.RequestContextHolder

/**
 * Represents common executor of the routing logic.
 */
abstract class AbstractRouteExecutor {

    /**
     * Controller inside of which execution is taking place.
     */
    def controller

    /**
     * Indicated whether activity closure should be executed after user is redirected to the next state or not.
     */
    Boolean activity

    /**
     * Redirect method parameters.
     */
    Map redirectParams

    /**
     * Controller to redirect user to.
     */
    String controllerToRedirect

    /**
     * Action to redirect user to.
     */
    String actionToRedirect

    /**
     * Event dispatcher.
     */
    EventDispatcher eventDispatcher

    /**
     * Token proxy.
     */
    TokenProxy tokenProxy

    /**
     * Entry repository.
     */
    EntryRepository entryRepository

    /**
     * Current flow scope.
     */
    FlowScope scope

    /**
     * Flow session holder.
     */
    HibernateSessionHolder flowSessionHolder

    /**
     * Token generator.
     */
    TokenGenerator tokenGenerator

    /**
     * Flow registry.
     */
    FlowRegistry flowRegistry

    /**
     * Flow execution listeners holder.
     */
    FlowExecutionListeners flowExecutionListeners

    /**
     * Executes routing logic in a basic way.
     */
    protected void basicExecute() {
        Entry registeredEntry = entryRepository.get()
        EntryValidator.validate(registeredEntry)

        def dispatcherToSet = registeredEntry.dispatcherStack

        def dispatchingConfiguration = DispatchingConfiguration.getConfigurationForAction(dispatcherToSet,
                controllerToRedirect, actionToRedirect)

        processDispatching(dispatcherToSet, dispatchingConfiguration)

        def activityToSet = activity != null ? activity : true

        handleDispatchItem(dispatchingConfiguration, activityToSet, dispatcherToSet)
    }

    /**
     * Processes dispatching logic basing on a current configuration.
     *
     * @param dispatcherStack dispatcher stack
     * @param dispatchingConfiguration current configuration
     */
    protected void processDispatching(Stack<EventDispatcher> dispatcherStack,
                                      DispatchingConfiguration dispatchingConfiguration) {
        try {
            if (dispatchingConfiguration.dispatchItem) {
                dispatcherStack.pop()
            }
        } catch (EmptyStackException ignore) {
        }
        if (eventDispatcher) {
            dispatcherStack.push(eventDispatcher)
        }
    }

    /**
     * Handles dispatch item.
     *
     * @param dispatchItem dispatch item
     * @param latestEventDispatcher the most recently added event dispatcher
     * @param activityToSet activity flag
     * @param dispatcherToSet event dispatcher to set
     * @param incrementToken indicates whether to increment snapshot token or not
     *
     * @throws FlowValidationException in case if an invalid dispatch item specified
     */
    protected void handleDispatchItem(DispatchingConfiguration dispatchingConfiguration,
                                      Boolean activityToSet,
                                      Stack<EventDispatcher> dispatcherToSet,
                                      Boolean incrementToken = false) throws FlowValidationException {
        def flowDataToSave = [:]
        flowDataToSave.putAll(scope.getData())
        def sessionToSave = flowSessionHolder.session
        def dispatchItem = dispatchingConfiguration.dispatchItem
        EventDispatcher latestEventDispatcher = dispatchingConfiguration.latestEventDispatcher
        if (!dispatchItem) {
            if (!isActionValidForExecution(controllerToRedirect, actionToRedirect)) {
                clearFlowAndRedirect(controllerToRedirect, actionToRedirect)
            } else {
                clearCurrentState(incrementToken)
                entryRepository.put(
                        new Entry(controllerToRedirect,
                                actionToRedirect,
                                new FlowSnapshotHolder(flowDataToSave, sessionToSave),
                                dispatcherToSet,
                                activityToSet)
                )

                routeToNextState(controllerToRedirect, actionToRedirect)
            }
        } else if (dispatchItem instanceof FlowAction) {
            if (!isActionValidForExecution(dispatchItem.controller, dispatchItem.action)) {
                clearFlowAndRedirect(dispatchItem.controller, dispatchItem.action)
            } else {
                clearCurrentState(incrementToken)
                entryRepository.put(
                        new Entry(dispatchItem.controller,
                                dispatchItem.action,
                                new FlowSnapshotHolder(flowDataToSave, sessionToSave),
                                dispatcherToSet,
                                activityToSet)
                )
                routeToNextState(dispatchItem.controller, dispatchItem.action)
            }
        } else if (dispatchItem instanceof Closure) {
            clearCurrentState(incrementToken)
            entryRepository.put(
                    new Entry(latestEventDispatcher.ownerController,
                            latestEventDispatcher.ownerAction,
                            new FlowSnapshotHolder(flowDataToSave, sessionToSave),
                            dispatcherToSet,
                            activityToSet,
                            dispatchItem)
            )

            routeToNextState(latestEventDispatcher.ownerController, latestEventDispatcher.ownerAction)
        } else {
            throw new FlowValidationException("Invalid dispatch item registered in event dispatcher")
        }
    }

    /**
     * Clears current web flow state.
     *
     * @param incrementToken indicates whether to increment snapshot token or not
     */
    protected void clearCurrentState(Boolean incrementToken) {
        flowExecutionListeners.pause()
        scope.clear()
        flowSessionHolder.clear()
        if (incrementToken) {
            tokenProxy.compoundToken = tokenGenerator.getNextToken()
        }
    }

    /**
     * Routes flow execution to the next state.
     *
     * @param controllerName next state controller name
     * @param actionName next state action name
     */
    protected void routeToNextState(String controllerName, String actionName) {
        def args = [:]
        redirectParams.put('execution', tokenProxy.compoundToken)
        args.put('params', redirectParams)
        args.put('controller', controllerName)
        args.put('action', actionName)

        controller.gdwfCachedRedirect(args)
    }

    /**
     * Clears flow and redirects user to the specified action.
     *
     * @param controllerName
     * @param actionName
     */
    protected void clearFlowAndRedirect(String controllerName, String actionName) {
        flowExecutionListeners.close()
        scope.clear()
        flowSessionHolder.clear()
        entryRepository.clear()
        def args = [:]
        args.put('params', redirectParams)
        args.put('controller', controllerName)
        args.put('action', actionName)

        controller.gdwfCachedRedirect(args)
    }

    /**
     * Indicates is specified action is a valid state for current flow.
     *
     * @param controllerName controller name
     * @param actionName action name
     * @return true, if specified action is a valid state for a flow execution, false otherwise
     */
    protected boolean isActionValidForExecution(String controllerName, String actionName) {
        FlowItem redirectFlowItem = flowRegistry.findByActionName(controllerName, actionName)
        Integer setupActionId = tokenProxy.flowToken
                .substring(GDWFConstraints.FLOW_TOKEN_PREFIX.length()) as Integer
        redirectFlowItem && (redirectFlowItem.stage == FlowStage.FLOW_STATE ||
                redirectFlowItem.flowId == setupActionId)
    }

    protected static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }
}
