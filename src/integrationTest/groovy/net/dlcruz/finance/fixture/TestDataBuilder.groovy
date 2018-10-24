package net.dlcruz.finance.fixture

import net.dlcruz.finance.dao.domain.JpaEntity
import net.dlcruz.finance.dao.service.base.EntityService
import org.springframework.data.jpa.repository.JpaRepository

abstract class TestDataBuilder<T extends JpaEntity<?>> {

    abstract T build()

    T persist(EntityService<T, ?> service) {
        service.create(build())
    }

    T persist(JpaRepository<T, ?> repository) {
        repository.save(build())
    }
}
