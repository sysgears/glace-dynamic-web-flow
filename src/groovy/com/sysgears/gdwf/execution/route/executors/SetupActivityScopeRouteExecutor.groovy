package com.sysgears.gdwf.execution.route.executors

import com.sysgears.gdwf.dispatch.EventDispatcher
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.route.AbstractRouteExecutor
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.FlowSnapshotHolder

/**
 * Provides execution of the routing logic in the setup activity execution scope.
 */
class SetupActivityScopeRouteExecutor extends AbstractRouteExecutor implements IExecutor {

    @Override
    void execute() {
        def dispatcherSnapshot = new Stack<EventDispatcher>()
        if (eventDispatcher) {
            dispatcherSnapshot.push(eventDispatcher)
        }
        def activityToSet = activity != null ? activity : true
        def sessionToSave = flowSessionHolder.session
        def flowDataToSave = [:]
        flowDataToSave.putAll(scope.getData())
        if (!isActionValidForExecution(controllerToRedirect, actionToRedirect)) {
            clearFlowAndRedirect(controllerToRedirect, actionToRedirect)
        } else {
            clearCurrentState(false)
            entryRepository.put(
                    new Entry(controllerToRedirect,
                            actionToRedirect,
                            new FlowSnapshotHolder(flowDataToSave, sessionToSave),
                            dispatcherSnapshot,
                            activityToSet)
            )
            routeToNextState(controllerToRedirect, actionToRedirect)
        }
    }
}