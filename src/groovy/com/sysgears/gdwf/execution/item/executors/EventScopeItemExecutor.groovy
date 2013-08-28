package com.sysgears.gdwf.execution.item.executors

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.exceptions.FlowValidationException
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.item.AbstractItemExecutor
import com.sysgears.gdwf.registry.Events
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.FlowSnapshotHolder

/**
 * Provides execution of the item logic in the events execution scope.
 */
class EventScopeItemExecutor extends AbstractItemExecutor implements IExecutor {

    @Override
    void execute() {
        // add token value to the request attributes,
        // so that the Grails' form and submitButton taglibs could imitate the WebFlow HTML pages
        request[GDWFConstraints.GDWF_TOKEN_VARIABLE_NAME] = tokenProxy.compoundToken
        Events defaultEvents = setupFlowItem.events
        Events events = flowItem.events.merge(defaultEvents, [GDWFConstraints.ACTIVITY_METHOD_NAME])
        Closure stateEvent = events.get(eventTriggered, stateOwner)
        if (!stateEvent) {
            // invalid event triggered
            throw new FlowValidationException("Event $eventTriggered is not a valid event for current state")
        } else {
            executeStateClosure(stateEvent)
            if (!request.isRedirected()) {
                def flowDataToSave = [:]
                flowDataToSave.putAll(scope.getData())
                def sessionToSave = flowSessionHolder.session

                flowExecutionListeners.pause()
                scope.clear()
                flowSessionHolder.clear()

                entryRepository.put(
                        new Entry(params.controller,
                                params.action,
                                new FlowSnapshotHolder(flowDataToSave, sessionToSave),
                                entry.dispatcherStack,
                                false)
                )

            }
        }
    }
}
