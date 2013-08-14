package com.sysgears.gdwf.token.holders

import com.sysgears.gdwf.GDWFConstraints

/**
 * Allows to access execution token value.
 */
class ExecutionTokenHolder extends AbstractCountableTokenHolder {

    /**
     * Initializes new execution token instance.
     */
    public ExecutionTokenHolder() {
        tokenPrefix = GDWFConstraints.EXECUTION_TOKEN_PREFIX
    }

    /**
     * Returns current value of execution counter, if it is not defined initializes new counter.
     *
     * @return current value of execution counter
     */
    int getCounter() {
        if (!storage.get(GDWFConstraints.GDWF_EXECUTION_COUNTER)) {
            storage.put(GDWFConstraints.GDWF_EXECUTION_COUNTER, 0)
        }
        storage.get(GDWFConstraints.GDWF_EXECUTION_COUNTER)
    }

    /**
     * Sets new value for execution counter.
     *
     * @param value value to set
     */
    void setCounter(int value) {
        storage.put(GDWFConstraints.GDWF_EXECUTION_COUNTER, value)
    }
}
