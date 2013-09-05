package com.sysgears.gdwf.exceptions

/**
 * Thrown to indicate that an invalid or no token or an invalid flow action was specified.
 */
class IncorrectStateException extends GDWFEntryException {

    /**
     * Constructs new exception instance with the specified detail message.
     *
     * @param message the detail message
     */
    IncorrectStateException(String message) {
        super(message)
    }
}
