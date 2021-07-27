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
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response


@ExtendWith(DropwizardExtensionsSupport::class)
class ProductResourceIntegrationTest {
    /*
    Modeling this test based on the example in the official docs Dropwizard v2.0.23:
    https://www.dropwizard.io/en/latest/manual/testing.html#integration-testing
     */

    private val EXT: DropwizardAppExtension<KotlinProductServiceConfiguration> = DropwizardAppExtension(
            KotlinProductServiceApplication::class.java,
            ResourceHelpers.resourceFilePath("product-service-test-config.yml")
    )

    @Test
    fun `GET single product`() {
        val client: Client = EXT.client()
        val result: Product = client.target("http://localhost:${EXT.localPort}/product/1")
                .request()
                .get(Product::class.java)
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.
        val expected = Product(1, "SKU-01", null, null)
        assertEquals(expected, result)
    }

    @Test
    fun `GET all products`() {
        val client: Client = EXT.client()
        val result: List<Product> = client.target("http://localhost:${EXT.localPort}/product")
                .request()
                .get(object: GenericType<List<Product>>() {})
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.
        val expected = listOf(
                Product(1, "SKU-01", null, null),
                Product(2, "SKU-02", null, null),
                Product(3, "SKU-03", null, null),
                Product(4, "SKU-04", null, null),
                Product(5, "SKU-05", null, null),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `POST to create product`() {
        val newProduct = Product(4, "SKU-04", null, null)

        val client: Client = EXT.client()
        val response: Response = client.target("http://localhost:${EXT.localPort}/product")
                .request()
                .post(Entity.json(newProduct))
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.

        assertEquals(200, response.status)
        val result: Product = response.readEntity(object: GenericType<Product>() {})
        assertEquals(newProduct, result)
    }

    @Test
    fun `PUT to update product`() {
        val updatedProduct = Product(1, "SKU-01_updated", null, null)

        val client: Client = EXT.client()
        val response: Response = client.target("http://localhost:${EXT.localPort}/product/${updatedProduct.productId}")
                .request()
                .put(Entity.json(updatedProduct))
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.

        assertEquals(200, response.status)
        val result: Product = response.readEntity(object: GenericType<Product>() {})
        assertEquals(updatedProduct, result)
    }

    @Test
    fun `PUT to update product with mismatch body and path param`() {
        val updatedProduct = Product(2, "SKU-01_updated", null, null)

        val client: Client = EXT.client()
        val response: Response = client.target("http://localhost:${EXT.localPort}/product/1")
                .request()
                .put(Entity.json(updatedProduct))
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.

        assertEquals(400, response.status)
    }

}