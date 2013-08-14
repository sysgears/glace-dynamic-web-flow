package com.sysgears.gdwf.execution.recall.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.recall.AbstractRecallExecutor

/**
 * Provides execution of the recall logic in the callback execution scope.
 */
class CallbackScopeRecallExecutor extends AbstractRecallExecutor implements IExecutor {

    @Override
    void execute() {
        basicExecute()
    }
}
