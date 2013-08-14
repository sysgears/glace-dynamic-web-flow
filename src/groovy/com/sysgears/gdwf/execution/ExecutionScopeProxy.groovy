package com.sysgears.gdwf.execution

import com.sysgears.gdwf.GDWFConstraints
import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides access to the current execution scope value. Execution scope is stored in the request instance.
 */
class ExecutionScopeProxy {

    /**
     * Returns current execution scope.
     *
     * @return execution scope if found, null otherwise
     */
    ExecutionScope getExecutionScope() {
        request[GDWFConstraints.STATE_EXECUTION_SCOPE] &&
                request[GDWFConstraints.STATE_EXECUTION_SCOPE] instanceof String ?
            ExecutionScope.getScope(request[GDWFConstraints.STATE_EXECUTION_SCOPE] as String) : null
    }

    /**
     * Sets current execution scope.
     *
     * @param scope
     */
    void setExecutionScope(ExecutionScope scope) {
        request[GDWFConstraints.STATE_EXECUTION_SCOPE] = scope.name
    }

    /**
     * Gets request.
     *
     * @return request
     */
    private static def getRequest() {
        RequestContextHolder.currentRequestAttributes().getCurrentRequest()
    }
}
