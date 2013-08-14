package com.sysgears.gdwf.execution.route.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.route.AbstractRouteExecutor
import com.sysgears.gdwf.execution.route.DispatchingConfiguration
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryValidator
import com.sysgears.gdwf.repository.FlowSnapshotHolder

/**
 * Provides execution of the routing logic in the events execution scope.
 */
class EventScopeRouteExecutor extends AbstractRouteExecutor implements IExecutor {

    @Override
    void execute() {
        Entry registeredEntry = entryRepository.get()
        EntryValidator.validate(registeredEntry)
        def dispatcherToSet = registeredEntry.dispatcherStack
        def dispatchingConfiguration = DispatchingConfiguration.getConfigurationForAction(dispatcherToSet,
                controllerToRedirect, actionToRedirect)

        processDispatching(dispatcherToSet, dispatchingConfiguration)
        def activityToSet = activity != null ? activity : true
        entryRepository.put(new Entry(params.controller,
                params.action,
                new FlowSnapshotHolder(scope.getData(), flowSessionHolder.session),
                registeredEntry.dispatcherStack,
                false)
        )

        handleDispatchItem(dispatchingConfiguration, activityToSet, dispatcherToSet, true)
    }
}
