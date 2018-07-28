package net.dlcruz.finance.dao.service.base

import net.dlcruz.finance.api.exception.ObjectValidationException
import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Frequency
import net.dlcruz.finance.dao.repository.base.BaseBudgetRepository
import net.dlcruz.finance.service.FrequencyService
import net.dlcruz.finance.service.MessageResolver

import javax.transaction.Transactional

abstract class BaseBudgetEntityService <EntityType extends Budget>
        extends BaseEntityService<EntityType, Long>
        implements BaseBudgetService<EntityType> {

    abstract protected MessageResolver getMessageResolver()

    abstract protected BaseBudgetRepository<EntityType> getRepository()

    abstract protected FrequencyService getFrequencyService()

    @Override
    EntityType create(EntityType entity) {
        super.create(entity)?.id?.with(this.&get)
    }

    @Override
    EntityType save(EntityType entity) {
        super.save(entity)?.id?.with(this.&get)
    }

    @Override
    @Transactional
    List<EntityType> findAllByAccount(Account account) {
        repository.findAllByAccount(account)
    }

    @Override
    boolean exist(EntityType entity) {
        getExisting(entity)
    }

    @Override
    EntityType convert(EntityType entity, Frequency targetFrequency) {
        def conversion = frequencyService.getConverter(entity.frequency, targetFrequency)
        entity.amount = conversion.call(entity.amount)
        entity.frequency = targetFrequency
        entity
    }

    @Override
    EntityType round(EntityType entity) {
        entity.amount = Math.ceil(entity.amount)
        entity
    }

    @Override
    ObjectValidationException validate(EntityType entity) {
        def validationException = doJpaValidation(entity)
        validateUniqueNameForAccount(entity, validationException)

        if (validationException.hasError) {
            throw validationException
        }
    }

    protected EntityType getExisting(EntityType entity) {
        if (entity.id == null) {
            repository.findByNameAndAccount(entity.name, entity.account)
        } else {
            repository.findByNameAndAccountAndIdNot(entity.name, entity.account, entity.id)
        }
    }

    private ObjectValidationException doJpaValidation(EntityType entity) {
        try {
            return super.validate(entity)
        } catch (ObjectValidationException ex) {
            return ex
        }
    }

    private void validateUniqueNameForAccount(EntityType entity, ObjectValidationException validationException) {
        def existingBudget = getExisting(entity)

        if (existingBudget) {
            def fieldName = 'name'
            def defaultMessage = messageResolver.getMessage(ObjectValidationException.UNIQUE_CONSTRAINT)
            validationException.pushUniqueConstraint(entity, fieldName, existingBudget, defaultMessage)
        }
    }
}