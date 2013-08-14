package com.sysgears.gdwf.mixins

import com.sysgears.gdwf.exceptions.FlowValidationException

/**
 * Wrapper for actual plugin methods, injected via metaprogramming.
 * Used in order to avoid naming conflicts.
 */
@Category(GDWFMixin)
class GDWFMethods {

    /**
     * Wrapper around the route method.
     *
     * @param args route arguments
     * @throws FlowValidationException in case if flow validation error occurred during execution
     */
    void route(Map args) throws FlowValidationException {
        callGDWFState(args)
    }

    /**
     * Wrapper around the route method.
     *
     * @param args route arguments
     * @param c event dispatcher holder
     * @throws FlowValidationException in case if flow validation error occurred during execution
     */
    void route(Map args, Closure c) throws FlowValidationException {
        callGDWFState(args, c)
    }

    /**
     * Wrapper around the reset method.
     *
     * @throws FlowValidationException in case if flow validation error occurred during execution
     */
    void reset() throws FlowValidationException {
        recallGDWFState()
    }
}