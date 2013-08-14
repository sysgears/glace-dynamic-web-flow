package com.sysgears.gdwf.storage

import spock.lang.Specification

class SessionStorageSpec extends Specification {

    def storage
    def session = [:]

    def setup() {
        storage = new Storage()
        storage.scopeAccessor = [getScope: {-> session }] as IScopeAccessor
        storage.tag = [getKeys: {-> ['repository', 'entry', 'snapshot']}] as IScopeLayerTag
    }

    def 'check whether storage is new'() {
        expect: 'scope for defined key set must not be initialized'
        storage.isNew()
    }

    def 'check whether storage is not new'() {
        when: 'put property to the storage and then call clear method'
        storage.put('prop1', 'value1')
        storage.clear()
        then: 'scope for defined key set must be initialized'
        !storage.isNew()
    }

    def 'check whether new storage is empty'() {
        expect: 'storage must be empty'
        storage.isEmpty()
    }

    def 'check whether storage is empty'() {
        when: 'put property to the storage and then call clear method'
        storage.put('prop1', 'value1')
        storage.clear()
        then: 'storage must be empty'
        storage.isEmpty()
    }

    def 'check whether storage is not empty'() {
        when: 'put property to the storage'
        storage.put('prop1', 'value1')
        then: 'storage must not be empty'
        !storage.isEmpty()
    }

    def 'get property value'() {
        when: 'put property to the storage'
        storage.put(property, value)
        then: 'property can be obtained by key'
        storage.get(property) == value
        where:
        property = 'prop'
        value = 'value'
    }

    def 'get property value on new storage'() {
        expect:
        storage.get('prop') == null
    }

    def 'get all properties'() {
        when: 'put a couple properties to the storage'
        storage.put(property1, value1)
        storage.put(property2, value2)
        then: 'specified properties are accessible by get all method'
        storage.getAll() == [(property1): value1, (property2): value2]
        where:
        property1 = "prop1"
        value1 = 'value1'
        property2 = 'prop2'
        value2 = 'value2'
    }

    def 'get all properties on new storage'() {
        expect:
        storage.getAll() == [:]
    }

    def 'clear storage'() {
        when: 'put property to the storage and then call crear method'
        storage.put('prop1', 'value1')
        storage.clear()
        then: 'storage is empty'
        storage.getAll() == [:]
    }

    def 'clear new storage'() {
        expect:
        storage.clear()
    }
}