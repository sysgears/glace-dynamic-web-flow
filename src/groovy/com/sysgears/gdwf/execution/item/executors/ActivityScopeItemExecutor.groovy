package com.sysgears.gdwf.execution.item.executors

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.item.AbstractItemExecutor
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.FlowSnapshotHolder

/**
 * Provides execution of the item logic in the activity execution scope.
 */
class ActivityScopeItemExecutor extends AbstractItemExecutor implements IExecutor {

    @Override
    void execute() {
        setGDWFResponseHeaders()
        // add token value to the request attributes,
        // so that the Grails' form and submitButton taglibs could imitate the WebFlow HTML pages
        request[GDWFConstraints.GDWF_TOKEN_VARIABLE_NAME] = tokenProxy.compoundToken

        String processedViewValue = flowItem.view?.isEmpty() ? entry.action : flowItem.view

        if (!entry.activity) {
            //render current action's view if no event triggered and entry was already visited
            controller.gdwfCachedRender(view: processedViewValue, model: entry.flowHolder.flow)
        } else {
            //execute action and pass the result scope in model
            def activityEvent = flowItem.events.get(GDWFConstraints.ACTIVITY_METHOD_NAME, stateOwner)
            executeStateClosure(activityEvent)
            //update snapshot for an item and mark as visited
            if (!request.isRedirected()) {
                def sessionToSave = flowSessionHolder.session
                entryRepository.put(
                        new Entry(entry.controller,
                                entry.action,
                                new FlowSnapshotHolder(scope.getData(), sessionToSave),
                                entry.dispatcherStack,
                                false)
                )
                flowExecutionListeners.pause()
                controller.gdwfCachedRender(view: processedViewValue, model: scope.getData())
                scope.clear()
                flowSessionHolder.clear()
            }
        }
    }
}
