package com.sysgears.gdwf.execution

import groovy.transform.TupleConstructor

/**
 * Holds executor along with the assigned execution scope.
 */
@TupleConstructor
class ExecutorDescriptor {

    /**
     * Indicates to which scope executor assigned to.
     */
    final ExecutionScope scope

    /**
     * Executor class.
     */
    final Class<? extends IExecutor> executorClass
}
