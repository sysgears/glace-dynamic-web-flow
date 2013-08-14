package com.sysgears.gdwf.storage

/**
 * Interface for classes that defines layer of storage scope.
 * <p>
 * Layer of the scope is a single element in multidimensional scope map.
 */
interface IScopeLayerTag {

    /**
     * Returns keys list which allows to access certain layer of storage scope.
     *
     * @return key list
     */
    List getKeys()
}
