package com.sysgears.gdwf.scope

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.storage.IScopeLayerTag

/**
 * Defines storage scope layer which contains flow scope properties.
 */
class FlowScopeLayerTag implements IScopeLayerTag {

    /**
     * Dependency injection for token proxy.
     */
    def tokenProxy

    /**
     * Returns keys list which defines scope layer for flow scope properties.
     *
     * @return key list
     */
    @Override
    List getKeys() {
        [GDWFConstraints.GDWF_SCOPE, "${tokenProxy.compoundToken}"]
    }
}
