package com.sysgears.gdwf.execution.item.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.item.AbstractItemExecutor

/**
 * Provides execution of the item logic in the callback execution scope.
 */
class CallbackScopeItemExecutor extends AbstractItemExecutor implements IExecutor {

    @Override
    void execute() {
        def callbackToExecute = entry.callback.rehydrate(controller, controller, controller)
        executeStateClosure(callbackToExecute)
        if (!request.isRedirected()) {
            flowExecutionListeners.pause()
            scope.clear()
            flowSessionHolder.clear()
        }
    }
}
