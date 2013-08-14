package com.sysgears.gdwf.token.holders

/**
 * Abstract token holder.
 */
abstract class AbstractCountableTokenHolder {

    /**
     * Dependency injection for storage object.
     */
    def storage

    /**
     * Token prefix.
     */
    String tokenPrefix

    /**
     * Returns current counter value, if it is not defined initializes new counter.
     *
     * @return current counter value
     */
    abstract int getCounter()

    /**
     * Sets new value for counter.
     *
     * @param value value to set
     */
    abstract void setCounter(int value)

    /**
     * Returns formatted token value.
     *
     * @return formatted token value
     */
    String getToken() {
        "${tokenPrefix}${counter}"
    }

    /**
     * Increments execution counter.
     */
    void incrementCounter() {
        counter = counter + 1
    }
}
