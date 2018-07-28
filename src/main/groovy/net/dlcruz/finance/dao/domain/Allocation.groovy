package net.dlcruz.finance.dao.domain

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = ['name', 'transaction_id']))
class Allocation implements JpaEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    String name

    String description

    @NotNull
    BigDecimal amount

    @ManyToOne
    @JsonIgnore
    Transaction transaction

    @ElementCollection
    Set<String> tags
}
