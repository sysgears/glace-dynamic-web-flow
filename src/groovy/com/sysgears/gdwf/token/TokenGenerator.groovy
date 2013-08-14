package com.sysgears.gdwf.token

import com.sysgears.gdwf.token.accessors.IFlowTokenAccessor
import com.sysgears.gdwf.token.holders.SnapshotTokenHolder
import groovy.transform.Synchronized
import groovy.util.logging.Log4j

/**
 * Token generator implementation. Uses HttpSession to keep the last execution and snapshot tokens value.
 */
@Log4j
class TokenGenerator {

    /**
     * Dependency injection for execution token holder.
     */
    def executionTokenHolder

    /**
     * Dependency injection for snapshot token holder. Uses id of the last execution to get snapshot counter.
     */
    def snapshotTokenHolder

    /**
     * Dependency injection for execution aware snapshot token holder. Unlike default token holder,
     * execution aware token holder uses current execution id to get snapshot counter.
     */
    def executionAwareSnapshotTokenHolder

    /**
     * Dependency injection for flow token accessor. Used for accessing the token for the new flow executions.
     */
    def flowTokenAccessor

    /**
     * Dependency injection for flow token accessor. Used for accessing the token for the existing flow executions.
     */
    def executionAwareFlowTokenAccessor

    /**
     * Returns the next token value.
     *
     * @param newExecution defines whether to generate a token value for new execution, false by default
     * @return the next token value
     */
    @Synchronized
    public String getNextToken(boolean newExecution = false) {
        SnapshotTokenHolder snapshotTokenHolder = newExecution ? this.snapshotTokenHolder : executionAwareSnapshotTokenHolder
        IFlowTokenAccessor flowTokenAccessor = newExecution ? this.flowTokenAccessor : executionAwareFlowTokenAccessor
        newExecution ? executionTokenHolder.incrementCounter() : snapshotTokenHolder.incrementCounter()

        "${flowTokenAccessor.flowToken}${executionTokenHolder.token}${snapshotTokenHolder.token}"
    }
}
