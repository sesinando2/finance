package net.dlcruz.finance.dao.service.base

import net.dlcruz.finance.api.exception.ObjectValidationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EntityService<EntityType, IdType> {

    Class<EntityType> getEntityClass()

    List<EntityType> list()

    Page<EntityType> listByPage(Pageable pageable)

    EntityType get(IdType id)

    EntityType create(EntityType entity)

    EntityType save(EntityType entity)

    void delete(EntityType entityType)

    ObjectValidationException validate(EntityType entityType)
}
