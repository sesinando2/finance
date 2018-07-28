package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.service.base.EntityService

interface AccountService extends EntityService<Account, Long> {

    boolean exist(Account account)
}
