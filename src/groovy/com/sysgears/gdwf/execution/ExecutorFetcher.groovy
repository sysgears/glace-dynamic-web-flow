package com.sysgears.gdwf.execution

import com.sysgears.gdwf.exceptions.FlowValidationException

/**
 * Provides logic for fetching proper executor for the current execution scope.
 */
class ExecutorFetcher {

    /**
     * Execution scope proxy.
     */
    def executionScopeProxy

    /**
     * Fetches an executor.
     *
     * @param executorDescriptors list of the executors descriptors to choose from
     * @return proper executor
     */
    Class<? extends IExecutor> fetch(List<ExecutorDescriptor> executorDescriptors) {
        ExecutionScope executionScope = executionScopeProxy.executionScope

        if (!executionScope) {
            throw new FlowValidationException("Invalid or no state execution scope found")
        }

        def executionDescriptor = executorDescriptors.find { it.scope == executionScope }

        if (!executionDescriptor) {
            throw new FlowValidationException("No executor specified for $executionScope execution scope")
        }

        executionDescriptor.executorClass
    }
}
