package com.sysgears.gdwf.storage

/**
 * Interface for classes that provide access to scope objects such as HTTP session or servlet context.
 */
interface IScopeAccessor {

    /**
     * Returns scope object.
     *
     * @return scope object
     */
    def getScope()
}
