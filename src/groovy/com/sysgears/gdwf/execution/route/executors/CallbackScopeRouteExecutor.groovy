package com.sysgears.gdwf.execution.route.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.route.AbstractRouteExecutor

/**
 * Provides execution of the routing logic in the callback execution scope.
 */
class CallbackScopeRouteExecutor extends AbstractRouteExecutor implements IExecutor {

    @Override
    void execute() {
        basicExecute()
    }
}