package honstain

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry
import io.micrometer.core.instrument.util.HierarchicalNameMapper
import io.micrometer.graphite.GraphiteConfig
import io.micrometer.graphite.GraphiteMeterRegistry
import java.time.Duration


class KotlinProductServiceApplication: Application<KotlinProductServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args: Array<String>) = KotlinProductServiceApplication().run(*args)
    }

    override fun getName(): String = "KotlinProductService"

    override fun run(config: KotlinProductServiceConfiguration, env: Environment) {


        val graphiteConfig: GraphiteConfig = object : GraphiteConfig {
            override fun host(): String {
                return "localhost"
            }

            override fun get(k: String): String? {
                return null // accept the rest of the defaults
            }

            override fun step(): Duration {
                return Duration.ofSeconds(1)
            }
        }

        val registry: MeterRegistry = GraphiteMeterRegistry(graphiteConfig, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT)

        val dropwizardMeterRegistry = object: DropwizardMeterRegistry(graphiteConfig, env.metrics(), HierarchicalNameMapper.DEFAULT, Clock.SYSTEM) {
            override fun nullGaugeValue(): Double? {
                return Double.NaN
            }
        }

        registry.config().commonTags("application", "ProductService")
        dropwizardMeterRegistry.config().commonTags("application", "ProductService")
        JvmMemoryMetrics().bindTo(registry)
        //Metrics.addRegistry(registry)
        Metrics.addRegistry(dropwizardMeterRegistry)

        // DropwizardMeterRegistry(graphiteConfig, env.metrics(), HierarchicalNameMapper.DEFAULT, Clock.SYSTEM) {}



        /*
        Had some trouble remembering how to interact with the object mapper.
        References:
        * https://github.com/dropwizard/dropwizard/issues/2580
        * https://www.dropwizard.io/en/latest/manual/internals.html
         */
        env.objectMapper.registerModule(KotlinModule())

        env.jersey().register(ProduceResource())
    }
}
