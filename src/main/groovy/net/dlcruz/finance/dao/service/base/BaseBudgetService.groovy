package net.dlcruz.finance.dao.service.base

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import net.dlcruz.finance.dao.domain.Frequency

interface BaseBudgetService<EntityType extends Budget> extends EntityService<EntityType, Long> {

    List<EntityType> findAllByAccount(Account account)

    boolean exist(EntityType budget)

    EntityType convert(EntityType budget, Frequency targetFrequency)

    EntityType round(EntityType budget)

    EntityType setAdditionalProperties(EntityType entityType)
}