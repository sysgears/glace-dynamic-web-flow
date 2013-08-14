package com.sysgears.gdwf.persistence

import org.hibernate.Session

/**
 * Holder for the Hibernate session.
 */
class HibernateSessionHolder {

    /**
     * Dependency injection for storage.
     */
    def storage

    /**
     * Key to get Hibernate session.
     */
    String key

    /**
     * Returns Hibernate session.
     *
     * @return Hibernate session
     */
    Session getSession() {
        storage.get(key) as Session
    }

    /**
     * Sets Hibernate session
     *
     * @param session session to set
     */
    void setSession(Session session) {
        storage.put(key, session)
    }

    /**
     * Clears session storage.
     */
    void clear() {
        storage.clear()
    }
}
