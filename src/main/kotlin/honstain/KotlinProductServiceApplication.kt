package honstain

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteReporter
import com.fasterxml.jackson.module.kotlin.KotlinModule
import honstain.kafkaProducer.ProductProducer
import io.dropwizard.Application
import io.dropwizard.kafka.KafkaProducerBundle
import io.dropwizard.kafka.KafkaProducerFactory
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.kafka.clients.producer.Producer
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit


class KotlinProductServiceApplication: Application<KotlinProductServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args: Array<String>) = KotlinProductServiceApplication().run(*args)
    }

    override fun getName(): String = "KotlinProductService"

    override fun initialize(bootstrap: Bootstrap<KotlinProductServiceConfiguration>) {
        super.initialize(bootstrap)

        bootstrap.addBundle(kafkaProducer)
    }

    override fun run(config: KotlinProductServiceConfiguration, env: Environment) {
        //val uniqueServiceId = 1 //UUID.randomUUID()
        //val graphite = Graphite(InetSocketAddress("localhost", 2003))
        //val reporter = GraphiteReporter.forRegistry(env.metrics())
        //        .prefixedWith("ProductService.$uniqueServiceId")
        //        .convertRatesTo(TimeUnit.SECONDS)
        //        .convertDurationsTo(TimeUnit.MILLISECONDS)
        //        .filter(MetricFilter.ALL)
        //        .build(graphite)
        //reporter.start(5, TimeUnit.SECONDS)

        /*
        Had some trouble remembering how to interact with the object mapper.
        References:
        * https://github.com/dropwizard/dropwizard/issues/2580
        * https://www.dropwizard.io/en/latest/manual/internals.html
         */
        env.objectMapper.registerModule(KotlinModule())

        val producer: Producer<String?, String?> = kafkaProducer.producer
        val productProducer = ProductProducer(producer)

        env.jersey().register(ProduceResource(productProducer, env.objectMapper))
        env.jersey().register(ProvenanceIDFilter())
    }

        private val topics: Collection<String> = listOf("quickstart-events")

        private val kafkaProducer: KafkaProducerBundle<String?, String?, KotlinProductServiceConfiguration> =
                        object : KafkaProducerBundle<String?, String?, KotlinProductServiceConfiguration>(topics) {

                                override fun getKafkaProducerFactory(configuration: KotlinProductServiceConfiguration): KafkaProducerFactory<String?, String?> {
                                        return configuration.getKafkaProducerFactory()!!
                                }
            }
}
