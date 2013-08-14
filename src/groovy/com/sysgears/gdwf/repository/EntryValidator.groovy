package com.sysgears.gdwf.repository

import com.sysgears.gdwf.exceptions.IncorrectStateException
import com.sysgears.gdwf.exceptions.NoSuchEntryException
import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides validation of the given entry.
 */
class EntryValidator {

    /**
     * Validates given entry.
     *
     * @param entry entry to validate
     * @throws IncorrectStateException in case if action specified in entry doesn't match current action
     * @throws NoSuchEntryException if entry is not found
     */
    static void validate(Entry entry) throws IncorrectStateException, NoSuchEntryException {
        if (!entry) {
            throw new NoSuchEntryException("Entry not found")
        }

        def params = RequestContextHolder.currentRequestAttributes().params
        if (entry.controller != params.controller ||
                entry.action != params.action) {
            throw new IncorrectStateException("""Controller and action parameters of entry,
                            registered with specified token, don't match current controller action.
                            Valid entry parameters: [controller: $entry.controller, action: $entry.action]""")
        }
    }
}
