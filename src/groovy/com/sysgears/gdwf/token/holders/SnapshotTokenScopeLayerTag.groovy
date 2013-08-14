package com.sysgears.gdwf.token.holders

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.storage.IScopeLayerTag

/**
 * Defines scope layer which contains snapshot token value.
 */
class SnapshotTokenScopeLayerTag implements IScopeLayerTag {

    /**
     * Dependency injection for execution token holder.
     */
    def executionTokenHolder

    /**
     * Dependency injection for flow token accessor.
     */
    def flowTokenAccessor

    /**
     * Returns keys list which defines scope layer for snapshot token.
     *
     * @return key list
     */
    @Override
    List getKeys() {
        [GDWFConstraints.GDWF_SNAPSHOTS_REPOSITORY, "${flowTokenAccessor.flowToken}${executionTokenHolder.token}"]
    }
}
