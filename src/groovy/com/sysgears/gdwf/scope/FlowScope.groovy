package com.sysgears.gdwf.scope

/**
 * Flow scope bean. Allows to access properties within a flow.
 */
class FlowScope {

    /**
     * Storage to hold properties.
     */
    def storage

    /**
     * Gets the property value.
     *
     * @param name property name
     * @return property value
     */
    def getProperty(String name) {
        storage.get(name)
    }

    /**
     * Sets the new value for the property.
     *
     * @param name property name
     * @param value new property value
     */
    void setProperty(String name, value) {
        storage.put(name, value)
    }

    /**
     * Fetches all the data stored by the current token.
     */
    Map getData() {
        storage.getAll()
    }

    /**
     * Stores provided flow to the storage.
     *
     * @param flow provided flow data
     */
    void setData(Map flow) {
        storage.putAll(flow)
    }

    /**
     * Checks whether the flow contains properties.
     *
     * @return true if the flow doesn't contain properties, false otherwise
     */
    boolean isEmpty() {
        storage.isEmpty()
    }

    /**
     * Removes all of the flow properties.
     */
    void clear() {
        storage.clear()
    }
}
