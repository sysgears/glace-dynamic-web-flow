package com.sysgears.gdwf.token.holders

import com.sysgears.gdwf.GDWFConstraints

/**
 * Allows to access snapshot token value.
 */
class SnapshotTokenHolder extends AbstractCountableTokenHolder {

    /**
     * Initializes new snapshot token instance.
     */
    public SnapshotTokenHolder() {
        tokenPrefix = GDWFConstraints.SNAPSHOT_TOKEN_PREFIX
    }

    /**
     * Returns current value of snapshot counter, if it is not defined initializes new counter.
     *
     * @return current value of snapshot counter
     */
    int getCounter() {
        if (!storage.get(GDWFConstraints.GDWF_SNAPSHOT_COUNTER)) {
            counter = 1
        }
        storage.get(GDWFConstraints.GDWF_SNAPSHOT_COUNTER)
    }

    /**
     * Sets new value for snapshot counter.
     *
     * @param value value to set
     */
    void setCounter(int value) {
        storage.put(GDWFConstraints.GDWF_SNAPSHOT_COUNTER, value)
    }
}
