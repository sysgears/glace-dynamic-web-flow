package com.sysgears.gdwf.execution.render.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.render.AbstractRenderExecutor

/**
 * Provides execution of the rendering logic in the activity execution scope.
 */
class ActivityScopeRenderExecutor extends AbstractRenderExecutor implements IExecutor {

    @Override
    void execute() {
        basicExecute()
    }
}
