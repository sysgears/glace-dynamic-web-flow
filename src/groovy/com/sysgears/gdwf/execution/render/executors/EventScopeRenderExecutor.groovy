package com.sysgears.gdwf.execution.render.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.render.AbstractRenderExecutor
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryValidator
import com.sysgears.gdwf.repository.FlowSnapshotHolder

/**
 * Provides execution of the rendering logic in the events execution scope.
 */
class EventScopeRenderExecutor extends AbstractRenderExecutor implements IExecutor {

    @Override
    void execute() {
        def modelArgs = renderArgs.model != null && renderArgs.model instanceof Map ? renderArgs.model : [:]
        def mergedModelMap = scope.getData() + modelArgs
        renderArgs.put('model', mergedModelMap)

        controller.gdwfCachedRender(renderArgs)
    }
}