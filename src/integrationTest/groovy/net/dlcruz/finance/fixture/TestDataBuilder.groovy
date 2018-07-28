package net.dlcruz.finance.fixture

abstract class TestDataBuilder<OutputType, BuilderType extends TestDataBuilder> {

    protected Map additionalProperties = [:]

    private OutputType entity

    protected TestDataBuilder(OutputType entity) {
        this.entity = entity
    }

    protected abstract OutputType doBuild()

    BuilderType build() {
        if (!entity) {
            this.entity = doBuild()
        }

        this
    }

    BuilderType additionalProperties(Map properties) {
        this.additionalProperties << properties
        this
    }

    OutputType getEntity() {
        if (!entity) {
            build()
        }

        entity
    }
}
