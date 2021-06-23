package honstain

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Environment

class KotlinProductServiceApplication: Application<KotlinProductServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args: Array<String>) = KotlinProductServiceApplication().run(*args)
    }

    override fun getName(): String = "KotlinProductService"

    override fun run(config: KotlinProductServiceConfiguration, env: Environment) {
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
