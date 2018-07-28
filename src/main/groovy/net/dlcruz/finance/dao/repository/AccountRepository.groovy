package net.dlcruz.finance.dao.repository

import net.dlcruz.finance.dao.domain.Account
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByOwnerOrderByNameAsc(String owner)

    Page<Account> findAllByOwnerOrderByNameAsc(String owner, Pageable pageable)

    Account findByNameAndOwner(String name, String owner)

    Account findByNameAndOwnerAndIdNot(String name, String owner, Long id)
}
