package com.sysgears.gdwf.registry

import com.sysgears.gdwf.FlowStage
import groovy.transform.ToString
import groovy.transform.TupleConstructor

/**
 * Holds registered flow action along with the GDWF specific details.
 */
@TupleConstructor
@ToString
class FlowItem {

    /**
     * Flow item id. Assigned only to the items of setup stage.
     */
    final Integer flowId

    /**
     * Flow action controller name
     */
    final String controller

    /**
     * Flow action name.
     */
    final String action

    /**
     * Assigned view.
     */
    final String view

    /**
     * Flow action stage.
     */
    final FlowStage stage

    /**
     * Registered events.
     */
    final Events events
}
