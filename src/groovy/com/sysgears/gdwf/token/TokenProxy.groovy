package com.sysgears.gdwf.token

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.exceptions.IncorrectStateException
import com.sysgears.gdwf.token.accessors.IFlowTokenAccessor
import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides access to token value. Token value is stored in the request params map.
 * <p>
 * Can be used as execution aware flow token accessor.
 */
class TokenProxy implements IFlowTokenAccessor {

    /**
     * Pattern to match the token against.
     */
    private final static TOKEN_MATCHING_REGEX = /${GDWFConstraints.FLOW_TOKEN_PREFIX}([0-9]+)${GDWFConstraints.EXECUTION_TOKEN_PREFIX}([0-9]+)${GDWFConstraints.SNAPSHOT_TOKEN_PREFIX}([0-9]+)/

    /**
     * Returns compound token.
     *
     * @return compound token value
     * @throws IncorrectStateException in case if there are no token specified
     */
    String getCompoundToken() throws IncorrectStateException {
        def compoundToken = params.execution instanceof String[] ? params.execution[0] : params.execution
        if (!compoundToken) {
            throw new IncorrectStateException("No flow token specified")
        }

        compoundToken
    }

    /**
     * Sets new compound token value.
     *
     * @param token value to set
     */
    void setCompoundToken(String token) {
        params.execution = token
    }

    /**
     * Returns setup action token value.
     *
     * @return setup action token value
     * @throws IncorrectStateException in case if there are no token specified
     */
    String getFlowToken() throws IncorrectStateException {
        def matcher = (compoundToken =~ TOKEN_MATCHING_REGEX)
        if (matcher.matches()) {
            "${GDWFConstraints.FLOW_TOKEN_PREFIX}${matcher[0][1]}"
        } else {
            throw new IncorrectStateException("Incorrectly formatted flow token: [$compoundToken]")
        }
    }

    /**
     * Returns execution token value.
     *
     * @return token value
     * @throws IncorrectStateException in case if there are no token specified
     */
    String getExecutionToken() throws IncorrectStateException {
        def matcher = (compoundToken =~ TOKEN_MATCHING_REGEX)
        if (matcher.matches()) {
            "${GDWFConstraints.EXECUTION_TOKEN_PREFIX}${matcher[0][2]}"
        } else {
            throw new IncorrectStateException("Incorrectly formatted flow token: [$compoundToken]")
        }
    }

    /**
     * Returns snapshot token value.
     *
     * @return snapshot token value
     * @throws IncorrectStateException in case if there are no token specified
     */
    String getSnapshotToken() throws IncorrectStateException {
        def matcher = (compoundToken =~ TOKEN_MATCHING_REGEX)
        if (matcher.matches()) {
            "${GDWFConstraints.SNAPSHOT_TOKEN_PREFIX}${matcher[0][3]}"
        } else {
            throw new IncorrectStateException("Incorrectly formatted flow token: [$compoundToken]")
        }
    }

    /**
     * Gets request params map.
     *
     * @return request params map
     */
    private static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }
}
