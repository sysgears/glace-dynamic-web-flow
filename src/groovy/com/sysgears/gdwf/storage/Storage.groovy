package com.sysgears.gdwf.storage

import com.sysgears.gdwf.GDWFConstraints
import groovy.util.logging.Log4j

/**
 * Stores properties in the specified scope layer.
 */
@Log4j
class Storage {

    /**
     * Dependency injection for scope layer tag.
     */
    IScopeLayerTag tag

    /**
     * Dependency injection for scope accessor.
     */
    IScopeAccessor scopeAccessor

    /**
     * Associates the specified value with the specified property.
     *
     * @param name property name
     * @param value new property value
     */
    void put(String property, value) {
        getScope(true)."$property" = value
        log.trace("Put value: [$value] to property: [$property] by key: [$tag.keys]")
    }

    /**
     * Returns the value to which the specified property is mapped.
     *
     * @param name property name
     * @return property value
     */
    def get(String property) {
        def value = scope?."$property"
        log.trace("Found mapped value: [$value] for property: [$property] by key: [$tag.keys]")
        value
    }

    /**
     * Fetches all the data stored in the storage.
     *
     * @return map representation of data stored in the storage
     */
    Map getAll() {
        Map values = scope ?: [:]
        log.trace("Found all the property values: [$values] by key: [$tag.keys]")
        values
    }

    /**
     * Puts all provided data to the storage.
     *
     * @param data provided data
     */
    void putAll(Map data) {
        getScope(true).putAll(data)
    }

    /**
     * Checks whether the storage is initialized.
     *
     * @return true if the storage is initialized, false otherwise
     */
    boolean isNew() {
        scope == null
    }

    /**
     * Checks whether the storage is empty.
     *
     * @return true if the storage is empty, false otherwise
     */
    boolean isEmpty() {
        !scope
    }

    /**
     * Removes all of the properties from storage.
     */
    void clear() {
        scope?.clear()
        log.trace("Cleared all the property values by key: $tag.keys")
    }

    /**
     * Returns scope layer for the specified key list.
     * <p>
     * For instance, the <pre>[repository, entry, snapshot]</pre> key list can be used to access the scope layer:
     * <pre>session.repository.entry.snapshot</pre>,
     *
     * @initialize if true and there is no scope layer for specified key set then new layer will be created
     * @return scope layer map or null
     */
    private Map getScope(boolean initialize = false) {
        def initMap = { scope, key ->
            if (!scope?."$key" && initialize) {
                scope?."$key" = [:]
            }
            scope?."$key"
        }
        def scope = scopeAccessor.scope
        [GDWFConstraints.GDWF_TAG].plus(tag.keys).each { key ->
            scope = initMap(scope, key)
        }

        scope as Map
    }
}
