package com.sysgears.gdwf.token.holders

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.storage.IScopeLayerTag

/**
 * Defines scope layer which contains execution token value.
 */
class ExecutionTokenScopeLayerTag implements IScopeLayerTag {

    /**
     * Returns keys list which defines scope layer for execution token.
     *
     * @return key list
     */
    @Override
    List getKeys() {
        [GDWFConstraints.GDWF_SNAPSHOTS_REPOSITORY]
    }
}
