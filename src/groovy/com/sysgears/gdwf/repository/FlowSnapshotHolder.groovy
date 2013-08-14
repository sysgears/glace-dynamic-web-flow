package com.sysgears.gdwf.repository

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.hibernate.Session

/**
 * Holds flow scope snapshot along with the attached Hibernate session.
 */
@TupleConstructor
@ToString
class FlowSnapshotHolder implements Serializable {

    /**
     * Flow scope snapshot.
     */
    Map flow

    /**
     * Attached Hibernate session.
     */
    Session session
}
