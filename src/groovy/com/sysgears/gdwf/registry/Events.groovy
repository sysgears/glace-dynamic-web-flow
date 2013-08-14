package com.sysgears.gdwf.registry

/**
 * Holds flow state event handlers.
 */
class Events {

    /**
     * Event handlers map. Key is an event name, for instance: next, prev or cancel. Value is an event handler closure.
     */
    private Map<String, Closure> _events = [:]

    /**
     * Initializes events.
     *
     * @param state closure that holds event handlers
     */
    Events(Closure state) {
        // call the closure to find events via the method missing
        state.resolveStrategy = Closure.DELEGATE_ONLY
        state.delegate = this
        state()
    }

    /**
     * Initializes events.
     *
     * @param events event handlers map
     */
    private Events(Map<String, Closure> events) {
        this._events << events
    }

    /**
     * Returns event handler by event name.
     *
     * @param name event name
     * @param owner the event handler owner
     * @return event handler
     */
    Closure get(String name, Closure owner) {
        rehydrateEvent(_events[name], owner)
    }

    /**
     * Merges the two events instances into one instance with all the event handlers of both event instances.
     *
     * @param events events to merge
     * @param exclude events excluded from merge
     * @return new events instance that holds merge result
     */
    Events merge(Events events, List<String> exclude = []) {
        Map<String, Closure> result = [:]
        result << _events
        result << events._events.findAll { event ->
            !_events.containsKey(event.key) && !exclude.contains(event.key)
        }
        new Events(result)
    }

    /**
     * Retrieves events from state closure.
     * <p>
     * Allows to use shortcuts: {@code event 'success'} or {@code event [controller: 'flow', action: 'create']}
     */
    def methodMissing(String name, args) {
        if (args.length == 1 && (args[0] instanceof String || args[0] instanceof Map)) {
            put(name, {-> callGDWFState(args[0]) })
        }
        if (args.length == 1 && args[0] instanceof Closure) {
            put(name, args[0] as Closure)
        }
    }

    /**
     * Adds new event handler.
     *
     * @param name event name
     * @param handler event handler
     */
    private put(String name, Closure handler) {
        _events.put(name, dehydrateEvent(handler))
    }

    /**
     * Dehydrates event handler closure.
     *
     * @param handler event closure to dehydrate
     * @return dehydrated event handler closure
     */
    private static Closure dehydrateEvent(Closure handler) {
        handler.dehydrate()
    }

    /**
     * Rehydrates event handler closure.
     *
     * @param handler event closure to rehydrate
     * @param owner the closure owner
     * @return rehydrated event handler closure
     */
    private static Closure rehydrateEvent(Closure handler, Closure owner) {
        handler?.rehydrate(owner, owner, owner.thisObject)
    }
}
