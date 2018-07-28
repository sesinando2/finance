package net.dlcruz.finance.api.controller.base

import net.dlcruz.finance.api.exception.EntityNotFoundException
import net.dlcruz.finance.dao.service.base.EntityService
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

abstract class BaseEntityController<EntityType, IdType> {

    @GetMapping
    List<EntityType> list() {
        service.list()
    }

    @GetMapping('/page')
    Page<EntityType> listByPage(Pageable pageable) {
        service.listByPage(pageable)
    }

    @GetMapping('/{id}')
    EntityType get(@PathVariable('id') IdType id) {
        getEntityAndThrowIfNotFound(id)
    }

    @PostMapping
    EntityType create(@RequestBody EntityType entity) {
        service.validate(entity)
        service.create(entity)
    }

    @PutMapping('/{id}')
    EntityType update(@PathVariable('id') IdType id, @RequestBody Map properties) {
        def entity = getEntityAndThrowIfNotFound(id)
        InvokerHelper.setProperties(entity, properties)
        service.validate(entity)
        service.save(entity)
    }

    @DeleteMapping('/{id}')
    void delete(@PathVariable('id') IdType id) {
        def entity = getEntityAndThrowIfNotFound(id)
        service.delete(entity)
    }

    protected abstract EntityService<EntityType, IdType> getService()

    protected EntityType getEntityAndThrowIfNotFound(IdType id) {
        def entity = service.get(id)

        if (!entity) {
            throw new EntityNotFoundException(service.entityClass, id)
        }

        entity
    }
}
