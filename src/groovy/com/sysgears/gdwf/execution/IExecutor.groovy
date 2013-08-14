package com.sysgears.gdwf.execution

import com.sysgears.gdwf.exceptions.FlowValidationException

/**
 * Represents web flow logic executor.
 */
public interface IExecutor {

    /**
     * Executes web flow logic.
     *
     * @throws FlowValidationException in case if flow validation error occurred
     */
    public void execute() throws FlowValidationException
}