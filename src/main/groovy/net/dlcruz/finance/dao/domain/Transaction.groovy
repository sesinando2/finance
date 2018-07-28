package net.dlcruz.finance.dao.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Formula

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Transaction implements JpaEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    Date date = new Date()

    String description

    @NotNull
    @ManyToOne
    @JsonIgnore
    Account account

    @OneToMany(cascade = CascadeType.ALL, mappedBy = 'transaction', orphanRemoval = true)
    Set<Allocation> allocations

    @Formula('(select coalesce(sum(a.amount), 0) from allocation a where a.transaction_id = id)')
    BigDecimal total
}
