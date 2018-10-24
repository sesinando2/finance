package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.JpaEntity
import net.dlcruz.finance.dao.service.base.EntityService
import org.codehaus.groovy.runtime.InvokerHelper

abstract class TestDataBuilder<T extends JpaEntity<?>> {

    private T entity

    protected EntityService<T, ?> service

    protected TestDataBuilder(EntityService<T, ?> service) {
        this.service = service
    }

    abstract T build()

    T persist() {
        if (!entity) {
            entity = service.create(build())
        }

        entity
    }

    T update() {
        def entity = persist()
        def newObject = build() as GroovyObject
        InvokerHelper.setProperties(entity, newObject.properties)
        service.save(entity)
    }

    T getEntity() {
        persist()
    }

    void setEntity(T entity) {
        this.entity
        reload()
    }

    protected void reload() {
        entity = service.get(entity.id)
    }
}
