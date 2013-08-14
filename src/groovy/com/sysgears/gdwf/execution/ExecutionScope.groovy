package com.sysgears.gdwf.execution

/**
 * Holds execution scopes.
 */
public enum ExecutionScope {

    /**
     * Indicates that execution is taking place on event trigger.
     */
    EVENTS_EXECUTION_SCOPE("events"),

    /**
     * Indicates that execution is taking place on activity closure execution.
     */
    ACTIVITY_EXECUTION_SCOPE("activity"),

    /**
     * Indicates that execution is taking place on callback closure execution.
     */
    CALLBACK_EXECUTION_SCOPE("callback"),

    /**
     * Indicates that execution is taking place on setup logic execution.
     */
    SETUP_ACTIVITY_EXECUTION_SCOPE("setupActivity")

    /**
     * Execution scope name.
     */
    private final String name

    /**
     * Creates new instance.
     *
     * @param name execution scope name
     */
    private ExecutionScope(String name) {
        this.name = name
    }

    /**
     * Gets execution scope by its string representation.
     *
     * @param name execution scope name
     * @return execution scope
     */
    static ExecutionScope getScope(String name) {
        values().find { it.name == name }
    }
}