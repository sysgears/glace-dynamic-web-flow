package com.sysgears.gdwf.execution.route

import com.sysgears.gdwf.dispatch.EventDispatcher
import groovy.transform.TupleConstructor

/**
 * Holds dispatching configuration.
 */
@TupleConstructor
class DispatchingConfiguration {

    /**
     * Item to route user to.
     */
    final def dispatchItem

    /**
     * Latest added event dispatcher.
     */
    final EventDispatcher latestEventDispatcher

    /**
     * Returns dispatching configuration for a specified action.
     *
     * @param dispatcherStack dispatcher stack
     * @param controllerName controller name
     * @param actionName action name
     * @return
     */
    public static getConfigurationForAction(Stack<EventDispatcher> dispatcherStack,
                                            String controllerName,
                                            String actionName) {
        def dispatchItem = null
        def latestEventDispatcher = null
        try {
            latestEventDispatcher = dispatcherStack.peek()
            dispatchItem = latestEventDispatcher.dispatcher?.entrySet()?.find {
                (!it.getKey().controller || (it.getKey().controller == controllerName)) &&
                        it.getKey().action == actionName
            }?.getValue()
        } catch (EmptyStackException ignore) {
        }

        new DispatchingConfiguration(dispatchItem, latestEventDispatcher)
    }
}
