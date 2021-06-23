import honstain.KotlinProductServiceApplication
import honstain.KotlinProductServiceConfiguration
import honstain.api.Product
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.client.Client


@ExtendWith(DropwizardExtensionsSupport::class)
class ProductResourceIntegrationTest {

    private val extension: DropwizardAppExtension<KotlinProductServiceConfiguration> = DropwizardAppExtension(
            KotlinProductServiceApplication::class.java,
            ResourceHelpers.resourceFilePath("product-service-test-config.yml")
    )

    @Test
    fun `GET single product`() {
        val client: Client = extension.client()
        val found: Product = client.target("http://localhost:${extension.localPort}/product/1")
                .request()
                .get(Product::class.java)
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.
        val expected = Product(1, "SKU-01", null, null)
        assertEquals(expected, found)
    }

}