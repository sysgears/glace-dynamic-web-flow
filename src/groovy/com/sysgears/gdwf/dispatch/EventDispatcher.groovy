package com.sysgears.gdwf.dispatch

import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides methods for registering and retrieving event dispatchers.
 * <p>
 * Uses HTTP session to hold event dispatchers stack.
 */
class EventDispatcher implements Serializable {

    /**
     * Name of the controller inside of which dispatcher registering is taking place.
     */
    String ownerController

    /**
     * Name of the action inside of which dispatcher registering is taking place.
     */
    String ownerAction

    /**
     * Holds dispatcher mapping.
     */
    Map<FlowAction, Object> dispatcher

    /**
     * Closure, inside of which dispatcher registering is taking place.
     */
    Closure c

    /**
     * Creates new instance.
     *
     * @param c closure, inside of which dispatcher registering is taking place
     */
    EventDispatcher(Closure c) {
        this.ownerController = params.controller
        this.ownerAction = params.action
        c.delegate = this
        c.resolveStrategy = Closure.DELEGATE_ONLY
        this.c = c
        c()
        this.c = null
    }

    /**
     * Creates new {@link EventMapper} instance.
     *
     * @param args "From" arguments that represents entity being mapped
     * @return new {@link EventMapper} instance
     */
    EventMapper map(Map args) {
        new EventMapper(args, c)
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
