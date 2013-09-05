package com.sysgears.gdwf.exceptions

/**
 * Represents an error occurred during the validation of the user entry.
 */
class GDWFEntryException extends Exception {

    /**
     * Constructs new exception instance with the specified detail message.
     *
     * @param message the detail message
     */
    GDWFEntryException(String message) {
        super(message)
    }
}
