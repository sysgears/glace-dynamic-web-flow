package com.sysgears.gdwf

/**
 * Holds plugin constraints.
 */
class GDWFConstraints {

    /**
     * Key to get GDWF properties for given scope accessor.
     */
    public static final String GDWF_TAG = 'com.sysgears.gdwf'

    /**
     * Key to access the flow scope properties.
     */
    public static final String GDWF_SCOPE = 'scope'

    /**
     * Key to access flow registry.
     */
    public static final String GDWF_REGISTRY = 'registry'

    /**
     * Key to access flow snapshots repository.
     */
    public static final String GDWF_SNAPSHOTS_REPOSITORY = 'snapshotsRepository'

    /**
     * Key to access hibernate session holder.
     */
    public static final String GDWF_HIBERNATE_SESSION = 'GDWFHibernateSession'

    /**
     * Key to save execution counter in the session.
     */
    public static final String GDWF_EXECUTION_COUNTER = 'executionCounter'

    /**
     * Key to save snapshot counter in the flow execution storage.
     */
    public static final String GDWF_SNAPSHOT_COUNTER = 'snapshotCounter'

    /**
     * Flow setup action token prefix
     */
    public static final String FLOW_TOKEN_PREFIX = 'f'

    /**
     * Flow execution token prefix.
     */
    public static final String EXECUTION_TOKEN_PREFIX = 'e'

    /**
     * Flow snapshot id prefix.
     */
    public static final String SNAPSHOT_TOKEN_PREFIX = 's'

    /**
     * Name of action method.
     */
    public static final String ACTIVITY_METHOD_NAME = 'activity'

    /**
     * Indicates scope where execution is taking place.
     */
    public static final String STATE_EXECUTION_SCOPE = 'stateExecutionScope'

    /**
     * Prefix for the event button names in the flow.
     * Used the same prefix as WebFlow plugin in order to avoid using specific taglib.
     */
    public static final String EVENT_PREFIX = '_eventId'

    /**
     * Prefix for the event button names in the flow in case if triggered via submitButton rendered element.
     * Used the same prefix as WebFlow plugin in order to avoid using specific taglib.
     */
    public static final String SUBMITBTN_EVENT_PREFIX = "${EVENT_PREFIX}_"

    /**
     * Name of the variable to save flow token in the request.
     * Used the same variable as WebFlow plugin in order to avoid using specific taglib.
     */
    public static final String GDWF_TOKEN_VARIABLE_NAME = 'flowExecutionKey'
}
