package com.sysgears.gdwf.exceptions

/**
 * Represents an error that was caused by an invalid usage of the flow constructions.
 */
class FlowValidationException extends Exception {

    /**
     * Constructs a new validation exception with the specified detail message.
     *
     * @param message the detail message
     */
    FlowValidationException(String message) {
        super(message)
    }
}
