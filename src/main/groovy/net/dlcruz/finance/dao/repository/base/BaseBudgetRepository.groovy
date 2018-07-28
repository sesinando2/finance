package net.dlcruz.finance.dao.repository.base

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Budget
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BaseBudgetRepository<EntityType extends Budget> extends JpaRepository<EntityType, Long> {

    @Query('''
        select b 
          from Budget b
         where b.account = :account
           and b.id not in (
               select g.id 
                 from Goal g
                 join g.account.transactions t
                where g.targetAmount <= (
                      select sum(a.amount) 
                        from Allocation a 
                       where a.transaction = t and a.name = g.name))''')
    List<EntityType> findAllByAccount(@Param('account') Account account)

    EntityType findByNameAndAccount(String name, Account account)

    EntityType findByNameAndAccountAndIdNot(String name, Account account, Long id)
}
