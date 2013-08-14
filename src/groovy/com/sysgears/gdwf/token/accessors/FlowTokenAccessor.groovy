package com.sysgears.gdwf.token.accessors

import com.sysgears.gdwf.GDWFConstraints
import org.springframework.web.context.request.RequestContextHolder

/**
 * Provides access to flow token value.
 */
class FlowTokenAccessor implements IFlowTokenAccessor {

    /**
     * Dependency injection for flow registry.
     */
    def flowRegistry

    /**
     * Returns flow token value.
     *
     * @return flow token value
     */
    @Override
    String getFlowToken() {
        def params = RequestContextHolder.currentRequestAttributes().params
        "${GDWFConstraints.FLOW_TOKEN_PREFIX}${flowRegistry.findByActionName(params.controller, params.action).flowId}"
    }
}
