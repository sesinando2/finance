package net.dlcruz.finance.dao.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Formula

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = ['name', 'account_id']))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type")
class Budget implements JpaEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @NotNull
    String name

    String description

    @NotNull
    BigDecimal amount

    @NotNull
    @Enumerated(EnumType.STRING)
    Frequency frequency

    @NotNull
    @ManyToOne
    @JsonIgnore
    Account account

    @Formula('(select coalesce(sum(a.amount), 0) from allocation a join transaction t on t.id = a.transaction_id where t.account_id = account_id and a.name = name)')
    BigDecimal balance = 0

    @Transient
    String getType() {
        getClass().simpleName
    }
}