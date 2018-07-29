databaseChangeLog {
    changeSet(id: '1531914156603-1', author: 'nandocruz (generated)') {
        createTable(tableName: 'account') {
            column(name: 'id', type: 'BIGINT', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'description', type: 'VARCHAR(255)')
            column(name: 'name', type: 'VARCHAR(255)') {
                constraints(nullable: false)
            }
            column(name: 'owner', type: 'VARCHAR(255)')
        }
    }

    changeSet(id: '1531914156603-2', author: 'nandocruz (generated)') {
        createTable(tableName: 'allocation') {
            column(name: 'id', type: 'BIGINT', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'amount', type: 'DECIMAL(19, 2)') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)')
            column(name: 'name', type: 'VARCHAR(255)') {
                constraints(nullable: false)
            }
            column(name: 'transaction_id', type: 'BIGINT')
        }
    }

    changeSet(id: '1531914156603-3', author: 'nandocruz (generated)') {
        createTable(tableName: 'allocation_tags') {
            column(name: 'allocation_id', type: 'BIGINT') {
                constraints(nullable: false)
            }
            column(name: 'tags', type: 'VARCHAR(255)')
        }
    }

    changeSet(id: '1531914156603-4', author: 'nandocruz (generated)') {
        createTable(tableName: 'budget') {
            column(name: 'type', type: 'VARCHAR(31)') {
                constraints(nullable: false)
            }
            column(name: 'id', type: 'BIGINT', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'amount', type: 'DECIMAL(19, 2)') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)')
            column(name: 'frequency', type: 'VARCHAR(255)') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(255)') {
                constraints(nullable: false)
            }
            column(name: 'target_amount', type: 'DECIMAL(19, 2)')
            column(name: 'target_date', type: 'datetime(6)')
            column(name: 'account_id', type: 'BIGINT') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(id: '1531914156603-5', author: 'nandocruz (generated)') {
        createTable(tableName: 'transaction') {
            column(name: 'id', type: 'BIGINT', autoIncrement: true) {
                constraints(primaryKey: true)
            }
            column(name: 'date', type: 'datetime(6)') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)')
            column(name: 'account_id', type: 'BIGINT') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(id: '1531914156603-6', author: 'nandocruz (generated)') {
        addUniqueConstraint(columnNames: 'name, owner', constraintName: 'UK3bvbdbdgl2tid2n80fkjqpaut', tableName: 'account')
    }

    changeSet(id: '1531914156603-7', author: 'nandocruz (generated)') {
        addUniqueConstraint(columnNames: 'name, transaction_id', constraintName: 'UKhoc6ieumce3hi4fgpi2v516hp', tableName: 'allocation')
    }

    changeSet(id: '1531914156603-8', author: 'nandocruz (generated)') {
        addUniqueConstraint(columnNames: 'name, account_id', constraintName: 'UKlogq3fig514yqbo8b6tju7b61', tableName: 'budget')
    }

    changeSet(id: '1531914156603-9', author: 'nandocruz (generated)') {
        createIndex(indexName: 'FK6g20fcr3bhr6bihgy24rq1r1b', tableName: 'transaction') {
            column(name: 'account_id')
        }
    }

    changeSet(id: '1531914156603-10', author: 'nandocruz (generated)') {
        createIndex(indexName: 'FKgc4360okmfe6jrt5e7glveyvs', tableName: 'allocation') {
            column(name: 'transaction_id')
        }
    }

    changeSet(id: '1531914156603-11', author: 'nandocruz (generated)') {
        createIndex(indexName: 'FKk4ld64gkavfh54hjsa69ks7l4', tableName: 'budget') {
            column(name: 'account_id')
        }
    }

    changeSet(id: '1531914156603-12', author: 'nandocruz (generated)') {
        createIndex(indexName: 'FKtp2ysqj5u06dv6ap8se44769a', tableName: 'allocation_tags') {
            column(name: 'allocation_id')
        }
    }

    changeSet(id: '1531914156603-13', author: 'nandocruz (generated)') {
        addForeignKeyConstraint(baseColumnNames: 'account_id', baseTableName: 'transaction', constraintName: 'FK6g20fcr3bhr6bihgy24rq1r1b', deferrable: false, initiallyDeferred: false, onDelete: 'NO ACTION', onUpdate: 'NO ACTION', referencedColumnNames: 'id', referencedTableName: 'account')
    }

    changeSet(id: '1531914156603-14', author: 'nandocruz (generated)') {
        addForeignKeyConstraint(baseColumnNames: 'transaction_id', baseTableName: 'allocation', constraintName: 'FKgc4360okmfe6jrt5e7glveyvs', deferrable: false, initiallyDeferred: false, onDelete: 'NO ACTION', onUpdate: 'NO ACTION', referencedColumnNames: 'id', referencedTableName: 'transaction')
    }

    changeSet(id: '1531914156603-15', author: 'nandocruz (generated)') {
        addForeignKeyConstraint(baseColumnNames: 'account_id', baseTableName: 'budget', constraintName: 'FKk4ld64gkavfh54hjsa69ks7l4', deferrable: false, initiallyDeferred: false, onDelete: 'NO ACTION', onUpdate: 'NO ACTION', referencedColumnNames: 'id', referencedTableName: 'account')
    }

    changeSet(id: '1531914156603-16', author: 'nandocruz (generated)') {
        addForeignKeyConstraint(baseColumnNames: 'allocation_id', baseTableName: 'allocation_tags', constraintName: 'FKtp2ysqj5u06dv6ap8se44769a', deferrable: false, initiallyDeferred: false, onDelete: 'NO ACTION', onUpdate: 'NO ACTION', referencedColumnNames: 'id', referencedTableName: 'allocation')
    }

}
