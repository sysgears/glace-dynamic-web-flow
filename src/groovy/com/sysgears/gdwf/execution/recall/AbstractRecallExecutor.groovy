package com.sysgears.gdwf.execution.recall

import com.sysgears.gdwf.exceptions.FlowValidationException
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.token.TokenProxy
import org.springframework.web.context.request.RequestContextHolder

/**
 * Represents common executor of the recall logic.
 */
abstract class AbstractRecallExecutor {

    /**
     * Controller inside of which execution is taking place.
     */
    def controller

    /**
     * Current flow scope.
     */
    FlowScope scope

    /**
     * Entry repository.
     */
    EntryRepository entryRepository

    /**
     * Flow session holder.
     */
    HibernateSessionHolder flowSessionHolder

    /**
     * Token proxy.
     */
    TokenProxy tokenProxy

    /**
     * Executes recall logic in a basic way.
     *
     * @throws FlowValidationException on method execution
     */
    protected void basicExecute() throws FlowValidationException {
        throw new FlowValidationException("""recallGDWFState method can only be called inside the events closure of the
            flow action""")
    }

    protected static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }
}
