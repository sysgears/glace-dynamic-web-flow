package com.sysgears.gdwf.execution.item

import com.sysgears.gdwf.execution.ExecutorDescriptor
import com.sysgears.gdwf.execution.ExecutorFetcher
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.persistence.FlowExecutionListeners
import com.sysgears.gdwf.registry.FlowItem
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.token.TokenGenerator
import com.sysgears.gdwf.token.TokenProxy

/**
 * Provides creation of the web flow item logic executors.
 */
class ItemExecutorFactory {

    /**
     * Token proxy.
     */
    TokenProxy tokenProxy

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
     * Token generator.
     */
    TokenGenerator tokenGenerator

    /**
     * Flow execution listeners holder.
     */
    FlowExecutionListeners flowExecutionListeners

    /**
     * Class that holds methods which provide GDWF plugin support.
     */
    Class webFlowMethodsClass

    /**
     * Helper instance, that provides fetching of the proper executor.
     */
    ExecutorFetcher executorFetcher

    /**
     * Current executors configuration.
     */
    List<ExecutorDescriptor> executorDescriptors

    /**
     * Creates new instance of the web flow item logic executor.
     *
     * @param controller controller instance
     * @param setupFlowItem setup flow item
     * @param flowItem flow item
     * @param stateOwner closure, that wraps closure with web flow state
     * @param eventTriggered event triggered
     * @param entry web flow entry
     *
     * @return executor instance
     */
    IExecutor create(def controller,
                     FlowItem setupFlowItem,
                     FlowItem flowItem,
                     Closure stateOwner,
                     String eventTriggered = null,
                     Entry entry = null) {
        Class<? extends IExecutor> executorClass = executorFetcher.fetch(executorDescriptors)

        executorClass.newInstance(controller: controller, setupFlowItem: setupFlowItem, flowItem: flowItem,
                eventTriggered: eventTriggered, entry: entry, stateOwner: stateOwner, tokenProxy: tokenProxy,
                entryRepository: entryRepository, scope: scope, flowSessionHolder: flowSessionHolder,
                tokenGenerator: tokenGenerator, flowExecutionListeners: flowExecutionListeners,
                webFlowMethodsClass: webFlowMethodsClass)
    }
}
