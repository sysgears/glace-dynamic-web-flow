package com.sysgears.gdwf.persistence

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.storage.IScopeLayerTag

/**
 * Defines scope layer which contains Hibernate session.
 */
class PersistenceScopeLayerTag implements IScopeLayerTag {

    /**
     * Dependency injection for token proxy.
     */
    def tokenProxy

    /**
     * Returns keys list which defines scope layer for hibernate session.
     *
     * @return key list
     */
    @Override
    List getKeys() {
        [GDWFConstraints.GDWF_HIBERNATE_SESSION, "${tokenProxy.compoundToken}"]
    }
}
