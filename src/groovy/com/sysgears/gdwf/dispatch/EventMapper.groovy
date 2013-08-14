package com.sysgears.gdwf.dispatch

import com.sysgears.gdwf.exceptions.FlowValidationException
import groovy.transform.TupleConstructor
import org.springframework.web.context.request.RequestContextHolder

/**
 * Helper class that maps events to certain functionality.
 */
@TupleConstructor
class EventMapper {

    /**
     * "From" arguments that represents entity that is being mapped.
     */
    final Map _args

    /**
     * Closure, inside of which mapping is taking place.
     */
    final Closure owner

    /**
     * Maps event to the specified flow action.
     *
     * @param args describes flow action to redirect user to. Should be specified the following way:<br>
     * <code>controller: "controllerName", action: "actionName"</code><br>
     * @throws FlowValidationException in case if arguments are describing Grails action in an invalid way
     */
    void to(Map args) throws FlowValidationException {
        def action = new FlowAction(getControllerNameForMapParameters(_args), getActionName(_args))
        def processedDispatchItem = new FlowAction(getControllerNameForToParameters(args), getActionName(args))
        dispatcher.put(action, processedDispatchItem)
    }

    /**
     * Maps event to the specified logic, encapsulated inside the closure.
     *
     * @param c closure to execute
     * @throws FlowValidationException in case if arguments are describing Grails action in an invalid way
     */
    void to(Closure c) throws FlowValidationException {
        def flowAction = new FlowAction(getControllerNameForMapParameters(_args), getActionName(_args))
        def processedDispatchItem = c.dehydrate()
        dispatcher.put(flowAction, processedDispatchItem)
    }

    /**
     * Get controller name for arguments specified in to() method call.
     *
     * @param args to() method arguments
     * @return controller name
     * @throws FlowValidationException in case if arguments are describing Grails action in an invalid way
     */
    private static String getControllerNameForToParameters(Map args) throws FlowValidationException {
        def flowActionControllerName = args.controller ?: params.controller
        if (!flowActionControllerName || !(flowActionControllerName instanceof String)) {
            throw new FlowValidationException("Argument controller missing or invalid in dispatcher description")
        }

        flowActionControllerName
    }

    /**
     * Gets controller name for the arguments specified in map() mathod call.
     *
     * @param args map() method arguments
     * @return controller name
     * @throws FlowValidationException in case if arguments are describing Grails action in an invalid way
     */
    private static String getControllerNameForMapParameters(Map args) throws FlowValidationException {
        def flowActionControllerName = args.controller
        if (flowActionControllerName && !(flowActionControllerName instanceof String)) {
            throw new FlowValidationException("Argument controller missing or invalid in dispatcher description")
        }

        flowActionControllerName
    }

    /**
     * Gets action name for the arguments specified in to() or map() method call.
     *
     * @param args map() or to() method arguments
     * @return action name
     * @throws FlowValidationException in case if arguments are describing Grails action in an invalid way
     */
    private static String getActionName(Map args) throws FlowValidationException {
        def flowActionName = args.action
        if (!flowActionName || !(flowActionName instanceof String)) {
            throw new FlowValidationException("Argument action missing or invalid in dispatcher description")
        }

        flowActionName
    }


    /**
     * Gets dispatcher map from the {@link EventDispatcher} object, initializes if not specified.
     *
     * @return dispatcher map
     */
    private Map getDispatcher() {
        if (!owner.delegate.dispatcher) {
            owner.delegate.dispatcher = [:]
        }

        owner.delegate.dispatcher
    }

    /**
     * Gets current request parameters.
     *
     * @return request parameters
     */
    private static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }
}
