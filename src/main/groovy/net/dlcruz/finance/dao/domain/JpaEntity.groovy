package net.dlcruz.finance.dao.domain

interface JpaEntity<IdType> {

    IdType getId()
}