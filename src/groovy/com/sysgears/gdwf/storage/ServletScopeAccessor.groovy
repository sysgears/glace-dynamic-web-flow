package com.sysgears.gdwf.storage

/**
 * Provides access to servlet context object.
 */
class ServletScopeAccessor implements IScopeAccessor {

    /**
     * Dependency injection for application context.
     */
    def applicationContext

    /**
     * Returns servlet context object.
     *
     * @return servlet context object {@link javax.servlet.ServletContext}
     */
    @Override
    def getScope() {
        applicationContext.servletContext
    }
}
