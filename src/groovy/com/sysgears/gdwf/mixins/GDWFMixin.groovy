package com.sysgears.gdwf.mixins

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.dispatch.EventDispatcher
import com.sysgears.gdwf.exceptions.FlowValidationException
import com.sysgears.gdwf.exceptions.IncorrectStateException
import com.sysgears.gdwf.exceptions.NoSuchEntryException
import com.sysgears.gdwf.execution.ExecutionScope
import com.sysgears.gdwf.execution.ExecutionScopeProxy
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.item.ItemExecutorFactory
import com.sysgears.gdwf.execution.recall.RecallExecutorFactory
import com.sysgears.gdwf.execution.render.RenderExecutorFactory
import com.sysgears.gdwf.execution.route.RouteExecutorFactory
import com.sysgears.gdwf.persistence.FlowExecutionListeners
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.registry.FlowItem
import com.sysgears.gdwf.registry.FlowRegistry
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.repository.EntryValidator
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.token.TokenProxy
import groovy.util.logging.Log4j
import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides methods that add GDWF plugin support to the controller.
 */
@Log4j
class GDWFMixin {

    /**
     * Gets the flow scope, The flow scope can be used within the flow actions as a temporary storage.
     */
    FlowScope getFlowScope() {
        applicationContext.getBean('gdwfScope')
    }

    /**
     * Overriden redirect method. Clears entry repository and flow scope if called inside the flow action.
     *
     * @param args redirect arguments
     */
    void redirect(Map args) {
        if (flowRegistry.findByActionName(params.controller, params.action)) {
            if (!request.isRedirected()) {
                flowExecutionListeners.close()
                flowScope.clear()
                flowSessionHolder.clear()
                entryRepository.clear()
                gdwfCachedRedirect(args)
            } else {
                throw new FlowValidationException("""Request can't be redirected more than once
                    inside the flow state""")
            }
        } else {
            gdwfCachedRedirect(args)
        }
    }

    /**
     * Overriden render method.
     *
     * @param args render arguments
     */
    void render(Map args) {
        if (flowRegistry.findByActionName(params.controller, params.action)) {
            IExecutor renderExecutor = renderExecutorFactory.create(this, args)
            renderExecutor.execute()
        } else {
            gdwfCachedRender(args)
        }
    }

    /**
     * Calls the next flow state.
     *
     * @param args describes flow action to redirect user to. Should be specified the following way:<br>
     * <code>controller: "controllerName", action: "actionName"</code>.<br>
     * <code>controller</code> parameter is not required
     * @param c event dispatcher
     */
    void callGDWFState(Map args, Closure c) {
        EventDispatcher eventDispatcher = new EventDispatcher(c)
        if (eventDispatcher.dispatcher && !eventDispatcher.dispatcher.isEmpty()) {
            callGDWFState(args, eventDispatcher)
        } else {
            callGDWFState(args)
        }
    }

    /**
     * Calls the next flow state.
     *
     * @param args describes flow action to redirect user to. Should be specified the following way:<br>
     * <code>controller: "controllerName", action: "actionName"</code><br>
     * <code>controller</code> parameter is not required.
     * @params eventDispatcher event dispatcher
     * @throws FlowValidationException in case if next action wasn't specified or if the method was called inside the
     * non-flow action
     */
    void callGDWFState(Map args, EventDispatcher eventDispatcher = null) throws FlowValidationException {
        def controllerToRedirect = args.controller ?: params.controller
        def actionToRedirect = args.action

        if (!actionToRedirect) {
            throw new FlowValidationException("Next flow action was not specified")
        }

        def flowItem = flowRegistry.findByActionName(params.controller, params.action)

        if (flowItem) {
            if (!request.isRedirected()) {
                Boolean activity = args.activity != null && args.activity instanceof Boolean ? args.activity : null
                Map redirectParams = args.params && args.params instanceof Map ? args.params : [:]

                IExecutor routeExecutor = routeExecutorFactory.create(this, activity, redirectParams,
                        controllerToRedirect, actionToRedirect, eventDispatcher)

                routeExecutor.execute()
            } else {
                throw new FlowValidationException("""Request can't be redirected more than once
                                    inside the flow state""")
            }
        } else {
            throw new FlowValidationException("callGDWFState method can only be called inside the flow action")
        }
    }

    /**
     * Recalls current state.
     *
     * @throws FlowValidationException in case if the method was called inside the non-flow action
     */
    void recallGDWFState() throws FlowValidationException {
        def flowItem = flowRegistry.findByActionName(params.controller, params.action)
        if (flowItem) {
            if (!request.isRedirected()) {
                IExecutor recallExecutor = recallExecutorFactory.create(this)

                recallExecutor.execute()
            } else {
                throw new FlowValidationException("""Request can't be redirected more than once
                                    inside the flow state""")
            }
        } else {
            throw new FlowValidationException("recallGDWFState method can only be called inside the events closure " +
                    "of flow action")
        }
    }

    /**
     * Adds flow setup functionality to the action inside of which the method is called.
     *
     * @param c closure to call inside the method
     * @throws FlowValidationException in case if current action is not registered in a flow registry
     */
    def executeGDWFSetupStage(Closure c) throws FlowValidationException {
        // get flow name for the current action
        FlowItem flowItem = flowItem
        try {
            if (isFlowExecuted()) {
                Integer setupActionId = tokenProxy.flowToken
                        .substring(GDWFConstraints.FLOW_TOKEN_PREFIX.length()) as Integer
                if (setupActionId == flowItem.flowId) {
                    executeState(flowItem, c, setupActionId, true)
                } else {
                    clearFlow()
                    executeSetupActivity(flowItem, c)
                }
            } else {
                executeSetupActivity(flowItem, c)
            }
        } catch (IncorrectStateException e) {
            log.info(e.message)
            executeSetupActivity(flowItem, c)
        }
    }

    /**
     * Adds flow state functionality to the action inside of which the method is called.
     *
     * @param c closure to call inside the method
     * @throws FlowValidationException in case if current action is not registered in a flow registry
     */
    def executeGDWFStateStage(Closure c) throws FlowValidationException {
        try {
            FlowItem flowItem = flowItem

            Integer setupActionId = tokenProxy.flowToken.substring(GDWFConstraints.FLOW_TOKEN_PREFIX.length()) as Integer
            executeState(flowItem, c, setupActionId, false)
        } catch (IncorrectStateException e) {
            log.info(e)
            response.sendError(404)
        }
    }

    /**
     * Executes state.
     *
     * @param flowItem current flow item
     * @param c closure to call
     * @param setupActionId setup action id
     * @param isSetupStage indicates whether current action a setup state
     */
    private void executeState(FlowItem flowItem, Closure c, Integer setupActionId, boolean isSetupStage) {
        def setupFlowItem = flowRegistry.findById(setupActionId)
        if (!setupFlowItem) {
            throw new IncorrectStateException("Flow setup action with id: $setupActionId doesn't exist")
        }

        try {
            Entry registeredEntry = entryRepository.get()
            EntryValidator.validate(registeredEntry)

            synchronized (registeredEntry) {

                String eventTriggered = null

                if (registeredEntry.callback) {
                    executionScopeProxy.executionScope = ExecutionScope.CALLBACK_EXECUTION_SCOPE
                } else if (!isSetupStage) {
                    // get triggered event
                    eventTriggered = params.keySet().findResult {
                        if (it.startsWith(GDWFConstraints.SUBMITBTN_EVENT_PREFIX) &&
                                it.length() > GDWFConstraints.SUBMITBTN_EVENT_PREFIX.length()) {
                            return it.substring(GDWFConstraints.SUBMITBTN_EVENT_PREFIX.length())
                        } else if (it == GDWFConstraints.EVENT_PREFIX) {
                            return params."$it"
                        }
                    }
                    //setting the state execution scope
                    executionScopeProxy.executionScope = eventTriggered ? ExecutionScope.EVENTS_EXECUTION_SCOPE :
                        ExecutionScope.ACTIVITY_EXECUTION_SCOPE
                } else {
                    executionScopeProxy.executionScope = ExecutionScope.ACTIVITY_EXECUTION_SCOPE
                }

                IExecutor itemExecutor = itemExecutorFactory.create(this, setupFlowItem, flowItem, c,
                        eventTriggered, registeredEntry)

                itemExecutor.execute()
            }
        } catch (NoSuchEntryException e) {
            log.info(e)
            clearFlow()
            isSetupStage ? executeSetupActivity(flowItem, c) :
                gdwfCachedRedirect(controller: setupFlowItem.controller, action: setupFlowItem.action)
        }
    }

    /**
     * Gets a flow item of the current action.
     *
     * @return flow item
     * @throws IncorrectStateException in case if controller or action can't be found in request parameters
     * @throws FlowValidationException in case if current action wasn't registered as a flow state
     */
    private FlowItem getFlowItem() throws IncorrectStateException, FlowValidationException {
        if (!params.controller || !params.action) {
            throw new IncorrectStateException("Controller or action wasn't found in request parameters")
        }

        // get flow name for the current action
        FlowItem flowItem = flowRegistry.findByActionName(params.controller, params.action)
        if (!flowItem) {
            throw new FlowValidationException("Action $params.action of controller $params.controller is not a " +
                    "registered flow action")
        }

        flowItem
    }

    /**
     * Executes setup activity logic.
     *
     * @param flowItem flow item
     * @param c closure to call
     */
    private void executeSetupActivity(FlowItem flowItem, Closure c) {
        executionScopeProxy.executionScope = ExecutionScope.SETUP_ACTIVITY_EXECUTION_SCOPE
        IExecutor itemExecutor = itemExecutorFactory.create(this, flowItem, flowItem, c)
        itemExecutor.execute()
    }

    /**
     * Clears flow, discards database changes.
     */
    private void clearFlow() {
        //reload flow
        flowExecutionListeners.discard()
        flowScope.clear()
        entryRepository.clear()
    }

    /**
     * Indicates whether the flow is being executed at the moment.
     *
     * @return true if flow is being executed, false otherwise
     */
    private boolean isFlowExecuted() {
        boolean result = true
        try {
            tokenProxy.compoundToken
        } catch (IncorrectStateException exc) {
            result = false
        }

        result
    }

    private static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }

    private static def getResponse() {
        RequestContextHolder.currentRequestAttributes().getCurrentResponse()
    }

    private RenderExecutorFactory getRenderExecutorFactory() {
        applicationContext.getBean('gdwfRenderExecutorFactory')
    }

    private RouteExecutorFactory getRouteExecutorFactory() {
        applicationContext.getBean('gdwfRouteExecutorFactory')
    }

    private ItemExecutorFactory getItemExecutorFactory() {
        applicationContext.getBean('gdwfItemExecutorFactory')
    }

    private RecallExecutorFactory getRecallExecutorFactory() {
        applicationContext.getBean('gdwfRecallExecutorFactory')
    }

    private FlowRegistry getFlowRegistry() {
        applicationContext.getBean('gdwfRegistry')
    }

    private TokenProxy getTokenProxy() {
        applicationContext.getBean('gdwfTokenProxy')
    }

    private EntryRepository getEntryRepository() {
        applicationContext.getBean('gdwfEntryRepository')
    }

    private ExecutionScopeProxy getExecutionScopeProxy() {
        applicationContext.getBean('gdwfExecutionScopeProxy')
    }

    private FlowExecutionListeners getFlowExecutionListeners() {
        applicationContext.getBean('gdwfFlowExecutionListeners')
    }

    private HibernateSessionHolder getFlowSessionHolder() {
        applicationContext.getBean('gdwfHibernateFlowExecutionListener').flowSessionHolder
    }
}
