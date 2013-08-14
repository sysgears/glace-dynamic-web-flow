package com.sysgears.gdwf.execution.recall.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.recall.AbstractRecallExecutor

/**
 * Provides execution of the recall logic in the activity execution scope.
 */
class ActivityScopeRecallExecutor extends AbstractRecallExecutor implements IExecutor {

    @Override
    void execute() {
        basicExecute()
    }
}
