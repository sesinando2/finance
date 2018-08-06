package net.dlcruz.finance.dao.service

import net.dlcruz.finance.dao.domain.Account
import net.dlcruz.finance.dao.domain.Breakdown
import net.dlcruz.finance.dao.domain.Frequency

interface BreakdownService {

    Breakdown getTotalBreakdown(Frequency frequency)

    Breakdown getTotalBreakdown(Frequency frequency, Account account)

    List<Breakdown> getBreakdown(Frequency frequency)

    List<Breakdown> getBreakdown(Frequency frequency, Account account)

    List<Breakdown> getTrendsFrom(Frequency frequency)

    List<Breakdown> getTrendsFrom(Frequency frequency, Account account)

    List<Breakdown> getTrendsFrom(Frequency frequency, int ago)

    List<Breakdown> getTrendsFrom(Frequency frequency, Account account, int ago)
}