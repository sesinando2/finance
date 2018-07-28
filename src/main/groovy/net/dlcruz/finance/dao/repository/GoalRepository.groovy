package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Goal
import net.dlcruz.finance.dao.repository.base.BaseBudgetRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GoalRepository extends BaseBudgetRepository<Goal> {

    @Query('''select b from Goal b where b.account = :account''')
    List<Goal> findAllByAccount(@Param('account') Account account)
}