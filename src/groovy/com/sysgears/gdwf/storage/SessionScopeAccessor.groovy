package com.sysgears.gdwf.storage

import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides access to HTTP session object.
 */
class SessionScopeAccessor implements IScopeAccessor {

    /**
     * Returns HTTP session object.
     *
     * @return HTTP session object {@link javax.servlet.http.HttpSession}
     */
    @Override
    def getScope() {
        RequestContextHolder.currentRequestAttributes().getSession()
    }
}
