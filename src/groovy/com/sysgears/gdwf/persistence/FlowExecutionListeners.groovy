package com.sysgears.gdwf.persistence

/**
 * Contains flow execution listeners.
 */
class FlowExecutionListeners {

    /**
     * Holds declared methods of listeners' interface.
     */
    private static List<String> DECLARED_METHODS = IFlowExecutionListener.class.getDeclaredMethods().collect { it.name }

    /**
     * Flow execution listeners list.
     */
    List listeners = []

    /**
     * Assures executing of the methods declared in the listener's interface.
     *
     * @param name method name
     * @param args method arguments
     * @return method execution result
     */
    def methodMissing(String name, args) {
        if (DECLARED_METHODS.contains(name) && args.length == 0) {
            listeners.each { it."$name"() }
        }
    }
}
