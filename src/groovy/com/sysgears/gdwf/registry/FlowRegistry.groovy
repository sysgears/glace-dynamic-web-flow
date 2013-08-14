package com.sysgears.gdwf.registry

import com.sysgears.gdwf.GDWFConstraints
import com.sysgears.gdwf.FlowStage
import com.sysgears.gdwf.storage.Storage
import groovy.util.logging.Log4j

/**
 * Provides methods for registering and retrieving flow items.
 * <p>
 * Uses application servlet context to hold flow item settings in {@link FlowItem}.
 */
@Log4j
class FlowRegistry {

    /**
     * Dependency injection for storage.
     */
    Storage storage

    /**
     * Registers the flow item within the application.
     *
     * @param controller name of item controller
     * @param action name of item action
     * @param view name of a view to render
     * @param stage item stage {@link FlowStage}
     * @param events flow item events {@link Events}
     */
    void registerGDWFItem(String controller, String action, String view, FlowStage stage, Events events) {
        def itemId = stage == FlowStage.FLOW_SETUP ?
            registry.findAll { it.stage == FlowStage.FLOW_SETUP }.size() + 1 : 0
        registerItem(new FlowItem(itemId, controller, action, view, stage, events))
        log.trace("Registered new flow item (controller: [$controller], action: [$action], stage: [$stage])")
    }

    /**
     * Finds flow item by id.
     *
     * @param id id to find by
     * @return flow item if item exist in the registry, null otherwise
     */
    FlowItem findById(Integer id) {
        registry.find { it.flowId == id }
    }

    /**
     * Finds flow item by action.
     *
     * @param controller controller name
     * @param action action name
     * @return flow item if item exist in the registry, null otherwise
     */
    FlowItem findByActionName(String controller, String action) {
        registry.find { it.controller == controller && it.action == action }
    }

    /**
     * Clears the registry.
     */
    void clear() {
        storage.clear()
        log.trace("Cleared flow registry")
    }

    /**
     * Gets registered flow items list.
     *
     * @return flow items list
     */
    private List<FlowItem> getRegistry() {
        if (!storage.get(GDWFConstraints.GDWF_REGISTRY)) {
            storage.put(GDWFConstraints.GDWF_REGISTRY, [])
        }

        storage.get(GDWFConstraints.GDWF_REGISTRY) as List
    }

    /**
     * Register flow item in a registry.
     *
     * @param item flow item to register
     */
    private void registerItem(FlowItem item) {
        def currentRegistry = registry
        currentRegistry << item
        storage.put(GDWFConstraints.GDWF_REGISTRY, currentRegistry)
    }
}
