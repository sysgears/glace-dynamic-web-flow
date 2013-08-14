package com.sysgears.gdwf.execution.recall

import com.sysgears.gdwf.execution.ExecutorDescriptor
import com.sysgears.gdwf.execution.ExecutorFetcher
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.token.TokenProxy

/**
 * Provides creation of the recall logic executors.
 */
class RecallExecutorFactory {

    /**
     * Current flow scope.
     */
    FlowScope scope

    /**
     * Entry repository.
     */
    EntryRepository entryRepository

    /**
     * Flow session holder.
     */
    HibernateSessionHolder flowSessionHolder

    /**
     * Token proxy.
     */
    TokenProxy tokenProxy

    /**
     * Helper instance, that provides fetching of the proper executor.
     */
    ExecutorFetcher executorFetcher

    /**
     * Current executors configuration.
     */
    List<ExecutorDescriptor> executorDescriptors

    /**
     * Creates new instance of the recall logic executor.
     *
     * @param controller controller instance
     * @return executor instance
     */
    public IExecutor create(def controller) {
        Class<? extends IExecutor> executorClass = executorFetcher.fetch(executorDescriptors)

        executorClass.newInstance(controller: controller, scope: scope, entryRepository: entryRepository,
                flowSessionHolder: flowSessionHolder, tokenProxy: tokenProxy)
    }
}
