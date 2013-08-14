package com.sysgears.gdwf.execution.render

import com.sysgears.gdwf.exceptions.FlowValidationException
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.scope.FlowScope
import org.springframework.web.context.request.RequestContextHolder

/**
 * Represents common executor of the rendering logic.
 */
abstract class AbstractRenderExecutor {

    /**
     * Entry repository.
     */
    EntryRepository entryRepository

    /**
     * Current flow scope.
     */
    FlowScope scope

    /**
     * Flow session holder.
     */
    HibernateSessionHolder flowSessionHolder

    /**
     * Controller instance.
     */
    def controller

    /**
     * Render method arguments.
     */
    Map renderArgs

    /**
     * Executes rendering logic in a basic way.
     *
     * @throws FlowValidationException on method execution
     */
    protected void basicExecute() throws FlowValidationException {
        throw new FlowValidationException("Render method can only be called inside the events closure")
    }

    protected static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }
}
