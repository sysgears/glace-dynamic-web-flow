package com.sysgears.gdwf.execution.item

import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.persistence.FlowExecutionListeners
import com.sysgears.gdwf.registry.FlowItem
import com.sysgears.gdwf.repository.Entry
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.repository.FlowSnapshotHolder
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.state.EventExecutor
import com.sysgears.gdwf.token.TokenGenerator
import com.sysgears.gdwf.token.TokenProxy
import org.springframework.web.context.request.RequestContextHolder

/**
 * Represents common executor of the web flow item logic.
 */
abstract class AbstractItemExecutor {

    /**
     * Controller inside of which execution is taking place.
     */
    def controller

    /**
     * Setup flow item.
     */
    FlowItem setupFlowItem

    /**
     * Flow item.
     */
    FlowItem flowItem

    /**
     * Event triggered.
     */
    String eventTriggered

    /**
     * Current flow entry.
     */
    Entry entry

    /**
     * Closure inside of which execution is taking place.
     */
    Closure stateOwner

    /**
     * Token proxy.
     */
    TokenProxy tokenProxy

    /**
     * Entry repository.
     */
    EntryRepository entryRepository

    /**
     * Current flow scope.
     */
    FlowScope scope

    /**
     * Flow session holder.
     */
    HibernateSessionHolder flowSessionHolder

    /**
     * Token generator.
     */
    TokenGenerator tokenGenerator

    /**
     * Flow execution listeners holder.
     */
    FlowExecutionListeners flowExecutionListeners

    /**
     * Class that holds methods which provide GDWF plugin support.
     */
    Class webFlowMethodsClass

    /**
     * Executes state closure.
     *
     * @param c state closure
     */
    protected void executeStateClosure(Closure c) {
        FlowSnapshotHolder flowHolder = entry.flowHolder
        scope.setData(flowHolder.flow)
        flowSessionHolder.session = flowHolder.session
        flowExecutionListeners.resume()
        use(webFlowMethodsClass) {
            EventExecutor.callEvent(c, params as Map)
        }
    }

    /**
     * Sets proper HTTP response headers that are forcing the browser
     * to refresh flow pages on browser back/forward buttons.
     */
    protected static void setGDWFResponseHeaders() {
        response.setHeader('Pragma', 'no-cache')
        response.setDateHeader('Expires', 1L)
        response.setHeader('Cache-Control', 'no-cache')
        response.addHeader('Cache-control', 'no-store')
    }

    protected static def getRequest() {
        RequestContextHolder.currentRequestAttributes().getCurrentRequest()
    }

    protected static def getResponse() {
        RequestContextHolder.currentRequestAttributes().getCurrentResponse()
    }

    protected static def getParams() {
        RequestContextHolder.currentRequestAttributes().params
    }
}
