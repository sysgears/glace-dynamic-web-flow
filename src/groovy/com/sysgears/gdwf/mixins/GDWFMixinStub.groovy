package com.sysgears.gdwf.mixins

import com.sysgears.gdwf.registry.Events

/**
 * Provides methods that allow to obtain event handlers for controller flow actions.
 */
class GDWFMixinStub {

    def executeGDWFSetupStage(Closure c) {
        new Events(c)
    }

    def executeGDWFStateStage(Closure c) {
        new Events(c)
    }
}