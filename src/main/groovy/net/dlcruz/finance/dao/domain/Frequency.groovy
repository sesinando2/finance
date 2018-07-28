package net.dlcruz.finance.dao.domain

import static com.google.common.base.CaseFormat.LOWER_HYPHEN
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE

enum Frequency {

    DAILY, WEEKLY, FORTNIGHTLY, MONTHLY, ANNUALLY

    static Frequency getFromPath(String frequencyString) {
        def frequencyName = LOWER_HYPHEN.to(UPPER_UNDERSCORE, frequencyString)
        Frequency.valueOf(frequencyName)
    }
}