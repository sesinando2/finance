package net.dlcruz.finance.dao.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Formula

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = ['name', 'owner']))
class Account implements JpaEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    String name

    String description

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = 'account', orphanRemoval = true)
    Set<Transaction> transactions

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = 'account', orphanRemoval = true)
    Set<Budget> budgets

    @JsonIgnore
    String owner

    @Formula('''(select coalesce(sum(a.amount), 0) from transaction t join allocation a on a.transaction_id = t.id where t.account_id = id)''')
    BigDecimal balance
}
