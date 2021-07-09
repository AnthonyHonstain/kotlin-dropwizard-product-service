package honstain

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteReporter
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.TimeUnit


class KotlinProductServiceApplication: Application<KotlinProductServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args: Array<String>) = KotlinProductServiceApplication().run(*args)
    }

    override fun getName(): String = "KotlinProductService"

    override fun run(config: KotlinProductServiceConfiguration, env: Environment) {
        val uniqueServiceId = UUID.randomUUID()
        val graphite = Graphite(InetSocketAddress("localhost", 2003))
        val reporter = GraphiteReporter.forRegistry(env.metrics())
                .prefixedWith("ProductService.$uniqueServiceId")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite)
        reporter.start(5, TimeUnit.SECONDS)

        /*
        Had some trouble remembering how to interact with the object mapper.
        References:
        * https://github.com/dropwizard/dropwizard/issues/2580
        * https://www.dropwizard.io/en/latest/manual/internals.html
         */
        env.objectMapper.registerModule(KotlinModule())

        env.jersey().register(ProduceResource())
        env.jersey().register(ProvenanceIDFilter())
    }
}
