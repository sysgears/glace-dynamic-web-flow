package com.sysgears.gdwf.repository

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.storage.IScopeLayerTag

/**
 * Defines scope layer which contains snapshots of the flow scope map.
 */
class EntryRepositoryScopeLayerTag implements IScopeLayerTag {

    /**
     * Dependency injection for token proxy.
     */
    def tokenProxy

    /**
     * Returns keys list which defines scope layer for the flow scope map snapshots.
     *
     * @return key list
     */
    @Override
    List getKeys() {
        [GDWFConstraints.GDWF_SNAPSHOTS_REPOSITORY, "${tokenProxy.flowToken}${tokenProxy.executionToken}"]
    }
}
