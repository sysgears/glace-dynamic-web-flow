package com.sysgears.gdwf.execution.recall.executors

import com.sysgears.gdwf.execution.IExecutor
import com.sysgears.gdwf.execution.recall.AbstractRecallExecutor
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryValidator
import com.sysgears.gdwf.repository.FlowSnapshotHolder

/**
 * Provides execution of the recall logic in the events execution scope.
 */
class EventScopeRecallExecutor extends AbstractRecallExecutor implements IExecutor {

    @Override
    void execute() {
        Entry registeredEntry = entryRepository.get()
        EntryValidator.validate(registeredEntry)

        entryRepository.put(
                new Entry(params.controller,
                        params.action,
                        new FlowSnapshotHolder(scope.getData(), flowSessionHolder.session),
                        registeredEntry.dispatcherStack,
                        false)
        )
        scope.clear()
        flowSessionHolder.clear()
        def args = [:]
        def paramsMap = [:]
        paramsMap.put('execution', tokenProxy.compoundToken)
        args.put('params', paramsMap)
        args.put('controller', params.controller)
        args.put('action', params.action)

        controller.gdwfCachedRedirect(args)
    }
}