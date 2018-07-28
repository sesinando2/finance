package net.dlcruz.finance.api.exception

class EntityNotFoundException extends Exception {

    Class type
    Serializable identifier

    EntityNotFoundException(Class type, Serializable identifier) {
        super("Could not find entity $type.simpleName with identifier $identifier")
        this.identifier = identifier
        this.type = type
    }
}
