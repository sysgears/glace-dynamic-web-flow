package com.sysgears.gdwf.execution.route.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.route.AbstractRouteExecutor

/**
 * Provides execution of the routing logic in the activity execution scope.
 */
class ActivityScopeRouteExecutor extends AbstractRouteExecutor implements IExecutor {

    @Override
    void execute() {
        basicExecute()
    }
}