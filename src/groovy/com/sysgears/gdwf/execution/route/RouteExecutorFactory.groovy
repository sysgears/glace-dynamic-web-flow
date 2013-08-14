package com.sysgears.gdwf.execution.route

import com.sysgears.gdwf.dispatch.EventDispatcher
import com.sysgears.gdwf.execution.ExecutorDescriptor
import com.sysgears.gdwf.execution.ExecutorFetcher
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.persistence.FlowExecutionListeners
import com.sysgears.gdwf.registry.FlowRegistry
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.token.TokenGenerator
import com.sysgears.gdwf.token.TokenProxy

/**
 * Provides creation of the routing logic executors.
 */
class RouteExecutorFactory {

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
     * Flow registry.
     */
    FlowRegistry flowRegistry

    /**
     * Flow execution listeners holder.
     */
    FlowExecutionListeners flowExecutionListeners

    /**
     * Helper instance, that provides fetching of the proper executor.
     */
    ExecutorFetcher executorFetcher

    /**
     * Current executors configuration.
     */
    List<ExecutorDescriptor> executorDescriptors

    /**
     * Creates new instance of the routing logic executor.
     *
     * @param controller controller instance
     * @param activity activity flag
     * @param redirectParams map specified in params parameter in redirect method arguments
     * @param controllerToRedirect controller to redirect user to
     * @param actionToRedirect action to redirect user to
     * @param eventDispatcher current event dispatcher
     * @return executor instance
     */
    IExecutor create(def controller,
                     Boolean activity,
                     Map redirectParams,
                     String controllerToRedirect,
                     String actionToRedirect,
                     EventDispatcher eventDispatcher) {
        Class<? extends IExecutor> executorClass = executorFetcher.fetch(executorDescriptors)

        executorClass.newInstance(controller: controller, activity: activity,
                redirectParams: redirectParams, controllerToRedirect: controllerToRedirect,
                    actionToRedirect: actionToRedirect, eventDispatcher: eventDispatcher, tokenProxy: tokenProxy,
                        entryRepository: entryRepository, scope: scope, flowSessionHolder: flowSessionHolder,
                            tokenGenerator: tokenGenerator, flowExecutionListeners: flowExecutionListeners,
                                flowRegistry: flowRegistry)
    }
}
