package com.sysgears.gdwf.execution.item.executors

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.exceptions.FlowValidationException
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.item.AbstractItemExecutor
import com.sysgears.gdwf.state.EventExecutor

/**
 * Provides execution of the item logic in the setup activity execution scope.
 */
class SetupActivityScopeItemExecutor extends AbstractItemExecutor implements IExecutor {

    @Override
    void execute() {
        tokenProxy.compoundToken = tokenGenerator.getNextToken(true)

        def actionEvent = flowItem.events.get(GDWFConstraints.ACTIVITY_METHOD_NAME, stateOwner)
        if (!actionEvent) {
            throw new FlowValidationException("No ${GDWFConstraints.ACTIVITY_METHOD_NAME} method defined for setup " +
                    "stage")
        }
        flowExecutionListeners.open()
        use(webFlowMethodsClass) {
            EventExecutor.callEvent(actionEvent, params as Map)
        }
        if (!request.isRedirected()) {
            flowExecutionListeners.pause()
            scope.clear()
            flowSessionHolder.clear()
        }
    }
}