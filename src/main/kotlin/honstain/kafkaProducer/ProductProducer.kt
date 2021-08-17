package honstain.kafkaProducer

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class ProductProducer(val producer: Producer<String?, String?>) {

    fun send(message: String) {
        producer.send(ProducerRecord("quickstart-events", message))
    }
}