package honstain


import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.kafka.KafkaProducerFactory
import javax.validation.Valid
import javax.validation.constraints.NotNull

class KotlinProductServiceConfiguration : Configuration() {

        private var kafkaProducerFactory: @Valid @NotNull KafkaProducerFactory<String?, String?>? = null

        @JsonProperty("producer")
        fun getKafkaProducerFactory(): KafkaProducerFactory<String?, String?>? {
                return kafkaProducerFactory
            }

        @JsonProperty("producer")
        fun setKafkaProducerFactory(producerFactory: KafkaProducerFactory<String?, String?>) {
                this.kafkaProducerFactory = producerFactory
            }
}