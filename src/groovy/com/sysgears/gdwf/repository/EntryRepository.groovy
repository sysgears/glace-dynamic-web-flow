package com.sysgears.gdwf.repository

import groovy.util.logging.Log4j

/**
 * Holds snapshots of thw flow scope map for every entry. Uses HttpSession object as a storage.
 */
@Log4j
class EntryRepository {

    /**
     * Dependency injection for token proxy.
     */
    def tokenProxy

    /**
     * Storage to hold snapshots.
     */
    def storage

    /**
     * Puts the flow entry to the repository.
     *
     * @param entry flow entry to store
     */
    void put(Entry entry) {
        storage.put(tokenProxy.snapshotToken, entry)
        log.trace("Added an entry (controller: [$entry.controller], action: [$entry.action]) to the repository, snapshot token: [$tokenProxy.snapshotToken]")
    }

    /**
     * Returns the flow entry
     *
     * @return flow snapshot
     */
    Entry get() {
        Entry entry = storage.get(tokenProxy.snapshotToken) as Entry
        if (entry) log.trace("Fetched an entry (controller: [$entry.controller], action: [$entry.action]) from the repository, snapshot token: [$tokenProxy.snapshotToken]")
        entry
    }

    /**
     * Clears entries.
     */
    void clear() {
        storage.clear()
        log.trace("Cleared entry repository")
    }
}
