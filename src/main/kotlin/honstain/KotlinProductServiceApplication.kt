package honstain

import io.dropwizard.Application
import io.dropwizard.setup.Environment

class KotlinProductServiceApplication: Application<KotlinProductServiceConfiguration>() {
    companion object {
        @JvmStatic fun main(args: Array<String>) = KotlinProductServiceApplication().run(*args)
    }

    override fun getName(): String = "KotlinProductService"

    override fun run(config: KotlinProductServiceConfiguration, env: Environment) {
        env.jersey().register(ProduceResource())
    }
}
