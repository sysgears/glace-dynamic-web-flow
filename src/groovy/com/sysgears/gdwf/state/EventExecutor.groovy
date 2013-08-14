package com.sysgears.gdwf.state

import org.codehaus.groovy.grails.web.metaclass.BindDynamicMethod

/**
 * Provides execution of an event.
 */
class EventExecutor {

    /**
     * Executes specified events.
     *
     * @param event event to execute
     * @param params passed parameters
     */
    static void callEvent(Closure event, Map params) {
        def eventParams = []
        event?.parameterTypes?.each { Class clazz ->
            if (clazz != Object.class) {
                def objectInstance = clazz.newInstance()
                BindDynamicMethod bind = new BindDynamicMethod()
                eventParams << bind.invoke(objectInstance, 'bind', (Object[]) [objectInstance, params])
            }
        }
        event?.call(* eventParams)
    }
}
