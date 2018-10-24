package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.JpaEntity
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.data.jpa.repository.JpaRepository

abstract class TestDataBuilder<T extends JpaEntity<?>> {

    private T entity

    protected JpaRepository<T, ?> repository

    protected TestDataBuilder(JpaRepository<T, ?> repository) {
        this.repository = repository
    }

    abstract T build()

    T persist() {
        if (!entity) {
            entity = repository.saveAndFlush(build())
        }

        entity
    }

    T update() {
        def entity = persist()
        def newObject = build() as GroovyObject
        def updates = newObject.properties
        updates.remove('id')
        InvokerHelper.setProperties(entity, updates)
        entity = repository.saveAndFlush(entity)
        entity
    }

    T getEntity() {
        persist()
    }
}
