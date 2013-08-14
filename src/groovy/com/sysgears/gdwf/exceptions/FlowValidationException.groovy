package com.sysgears.gdwf.exceptions

/**
 * Thrown if there was an invalid entry to the flow action or method.
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
