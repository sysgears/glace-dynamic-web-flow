package com.sysgears.gdwf.persistence

import com.sysgears.gdwf.exceptions.FlowValidationException
import org.hibernate.FlushMode
import org.hibernate.Session
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate

/**
 * Provides helper methods to manage hibernate session.
 */
class HibernateFlowExecutionListener implements IFlowExecutionListener {

    /**
     * Dependency injection for session factory.
     */
    def sessionFactory

    /**
     * Dependency injection for transaction manager.
     */
    def transactionManager

    /**
     * Dependency injection for request session holder.
     */
    def requestSessionHolder

    /**
     * Dependency injection for flow session holder.
     */
    def flowSessionHolder

    /**
     * Creates new hibernate session and binds it to the thread.
     */
    public void open() {
        // check the cache
        if (requestSessionHolder.session || flowSessionHolder.session) throw new FlowValidationException(
                'Illegal call of start event, session cache is not empty')
        // unbind and cache request session
        requestSessionHolder.session = unbind()
        if (!requestSessionHolder.session) throw new FlowValidationException('Illegal call of start event, there is no request session to unbind')
        // create and cache new flow session
        flowSessionHolder.session = createSession()
        // bind the flow session to the thread
        bind(flowSessionHolder.session)
    }

    /**
     * Unbinds hibernate session from the thread.
     */
    public void pause() {
        // check the cache
        if (!requestSessionHolder.session) throw new FlowValidationException(
                'Illegal call of pause event, session cache does not contain request session to bind')
        if (!flowSessionHolder.session) throw new FlowValidationException(
                'Illegal call of pause event, there is no flow session to unbind')
        // unbid flow session
        unbind().disconnect()
        // get request session from the cache and bind it to the thread
        bind(requestSessionHolder.session)
        // remove request session from the cache
        requestSessionHolder.session = null
    }

    /**
     * Binds hibernate session to the thread.
     */
    public void resume() {
        // check the cache
        if (requestSessionHolder.session) throw new FlowValidationException(
                'Illegal call of resume event, session cache should not contain request session')
        if (!flowSessionHolder.session) throw new FlowValidationException(
                'Illegal call of resume event, there is no flow session to bind')
        // unbind and cache request session
        requestSessionHolder.session = unbind()
        if (!requestSessionHolder.session) throw new FlowValidationException('Illegal call of resume event, there is no request session to unbind')
        // get flow session from the cache bind it to the thread
        bind(flowSessionHolder.session)
    }

    /**
     * Flushes changes to database and unbinds session from the thread.
     */
    public void close() {
        // check the cache
        if (!requestSessionHolder.session) throw new FlowValidationException(
                'Illegal call of close event, session cache does not contain request session to bind')
        if (!flowSessionHolder.session) throw new FlowValidationException(
                'Illegal call of close event, there is no flow session to flush')
        // flush the flow session
        new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                sessionFactory.currentSession
            }
        })
        // unbind and close flow session
        unbind().close()
        // bind request session
        bind(requestSessionHolder.session)
        // clear the cache
        flowSessionHolder.session = null
        requestSessionHolder.session = null
    }

    /**
     * Discards changes.
     */
    public void discard() {
        if (flowSessionHolder.session) {
            // there is flow session to close
            if (requestSessionHolder.session) {
                // flow session has been bound to the thread
                pause()
            }
            flowSessionHolder.session.close()
            flowSessionHolder.session = null
        }
    }

    /**
     * Create new hibernate session.
     *
     * @return new hibernate session
     */
    private Session createSession() {
        Session session = sessionFactory.openSession()
        session.flushMode = FlushMode.MANUAL
        session
    }

    /**
     * Binds hibernate session to the thread.
     *
     * @param session session to bind
     */
    private void bind(Session session) {
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
    }

    /**
     * Unbind current hibernate session from the thread.
     *
     * @return unbound hibernate session or null
     */
    private Session unbind() {
        Session session = null
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            session = TransactionSynchronizationManager.getResource(sessionFactory).session
            TransactionSynchronizationManager.unbindResource(sessionFactory)
        }

        session
    }
}
