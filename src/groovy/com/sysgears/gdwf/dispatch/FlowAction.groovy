package com.sysgears.gdwf.dispatch

import groovy.transform.ToString
import groovy.transform.TupleConstructor


/**
 * Represents flow action entity.
 */
@ToString
@TupleConstructor
class FlowAction implements Serializable {

    /**
     * Flow action controller name.
     */
    String controller

    /**
     * Flow action name.
     */
    String action
}
