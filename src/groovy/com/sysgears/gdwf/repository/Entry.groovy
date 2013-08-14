package com.sysgears.gdwf.repository

import com.sysgears.gdwf.dispatch.EventDispatcher

/**
 * Holds flow action user entry details.
 */
class Entry {

    /**
     * Flow action controller name.
     */
    final String controller

    /**
     * Flow action name.
     */
    final String action

    /**
     * Callback closure.
     */
    final Closure callback

    /**
     * Indicates whether an entry a recall of already executed state or not.
     */
    boolean activity

    /**
     * Serialized flow data for current entry.
     */
    private final byte[] flowSnapshot

    /**
     * Serialized dispatcher for current entry.
     */
    private final byte[] dispatcherSnapshot

    /**
     * Creates new instance.
     *
     * @param controller controller action name
     * @param action action name
     * @param flow flow scope data
     * @param activity indicates whether an entry a recall
     */
    Entry(String controller,
          String action,
          FlowSnapshotHolder flowSnapshot,
          Stack<EventDispatcher> dispatcherSnapshot,
          Boolean activity = false,
          Closure callback = null) {
        this.controller = controller
        this.action = action
        this.flowSnapshot = serializeEntryObject(flowSnapshot)
        this.dispatcherSnapshot = serializeEntryObject(dispatcherSnapshot)
        this.callback = callback
        this.activity = activity
    }

    /**
     * Deserializes and returns flow scope data.
     *
     * @return deserialized flow scope data
     */
    public FlowSnapshotHolder getFlowHolder() {
        deserializeEntryObject(flowSnapshot) as FlowSnapshotHolder
    }

    public Stack<EventDispatcher> getDispatcherStack() {
        deserializeEntryObject(dispatcherSnapshot) as Stack
    }

    private static byte[] serializeEntryObject(def object) {
        FlowSerializer.compress(FlowSerializer.serialize(object))
    }

    private static def deserializeEntryObject(byte[] data) {
        FlowSerializer.deserialize(FlowSerializer.decompress(data))
    }
}