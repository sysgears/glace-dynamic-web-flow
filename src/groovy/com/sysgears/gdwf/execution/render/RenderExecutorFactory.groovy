package com.sysgears.gdwf.execution.render

import com.sysgears.gdwf.execution.ExecutorDescriptor
import com.sysgears.gdwf.execution.ExecutorFetcher
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.scope.FlowScope

/**
 * Provides creation of the rendering logic executors.
 */
class RenderExecutorFactory {

    /**
     * Entry repository.
     */
    EntryRepository entryRepository

    /**
     * Current flow scope.
     */
    FlowScope scope

    /**
     * Flow session holder.
     */
    HibernateSessionHolder flowSessionHolder

    /**
     * Helper instance, that provides fetching of the proper executor.
     */
    ExecutorFetcher executorFetcher

    /**
     * Current executors configuration.
     */
    List<ExecutorDescriptor> executorDescriptors

    /**
     * Creates new instance of the rendering logic executor.
     *
     * @param controller instance
     * @param renderArgs render method arguments
     * @return executor instance
     */
    IExecutor create(def controller, Map renderArgs) {
        Class<? extends IExecutor> executorClass = executorFetcher.fetch(executorDescriptors)

        executorClass.newInstance(entryRepository: entryRepository, scope: scope,
                flowSessionHolder: flowSessionHolder, controller: controller, renderArgs: renderArgs)
    }
}
