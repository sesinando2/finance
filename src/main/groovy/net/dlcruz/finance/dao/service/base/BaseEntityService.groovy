package net.dlcruz.finance.dao.service.base

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.JpaEntity
import net.dlcruz.finance.dao.service.implementation.EntityValidationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

import javax.transaction.Transactional

abstract class BaseEntityService<EntityType extends JpaEntity, IdType> implements EntityService<EntityType, IdType> {

    @Override
    List<EntityType> list() {
        repository.findAll()
    }

    @Override
    Page<EntityType> listByPage(Pageable pageable) {
        repository.findAll(pageable)
    }

    @Override
    EntityType get(IdType id) {
        repository.findOne(id)
    }

    @Override
    @Transactional
    EntityType create(EntityType entity) {
        assert entity.id == null
        save(entity)
    }

    @Override
    @Transactional
    EntityType save(EntityType entity) {
        repository.save(entity)
    }

    @Override
    @Transactional
    void delete(EntityType entity) {
        repository.delete(entity.id)
    }

    @Override
    Class<EntityType> getEntityClass() {
        EntityType.class
    }

    @Override
    ObjectValidationException validate(EntityType entity) {
        def exception = new ObjectValidationException(entity)
        exception << validationService.validate(entity)

        if (exception.hasError) throw exception

        exception
    }

    protected abstract JpaRepository<EntityType, IdType> getRepository()

    protected abstract EntityValidationService getValidationService()
}
