import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import honstain.ProduceResource
import honstain.api.Product
import honstain.kafkaProducer.ProductProducer
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import io.dropwizard.testing.junit5.ResourceExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response


@ExtendWith(DropwizardExtensionsSupport::class)
class ProductResourceTest {
    /*
    Modeling this test based on the example in the official docs Dropwizard v2.0.23:
    https://www.dropwizard.io/en/latest/manual/testing.html#testing-resources
     */
    val productProducer = mockk<ProductProducer>()
    val objectMapper = ObjectMapper().registerModule(KotlinModule())
    val EXT: ResourceExtension = ResourceExtension.builder()
            .addResource(ProduceResource(productProducer, objectMapper))
            .setMapper(objectMapper)
            .build()

    @Test
    fun `GET single product`() {
        val result = EXT.target("/product/1").request().get(Product::class.java)
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.

        val expected = Product(1, "SKU-01", null, null)
        assertEquals(expected, result)
    }

    @Test
    fun `POST create product`() {
        val newProduct = Product(4, "SKU-04", null, null)
        val productJSON = "{" +
                "\"productId\":4," +
                "\"sku\":\"SKU-04\"," +
                "\"barcode\":null," +
                "\"taxCode\":null" +
                "}"
        every { productProducer.send(productJSON) } returns Unit

        val response: Response = EXT.target("/product").request().post(Entity.json(newProduct))
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.

        assertEquals(200, response.status)
        val result: Product = response.readEntity(object: GenericType<Product>() {})
        assertEquals(newProduct, result)
        verify(exactly = 1) { productProducer.send(productJSON) }
    }

    @Test
    fun `PUT to update product`() {
        val updatedProduct = Product(1, "SKU-01_updated", null, null)

        val response: Response = EXT.target("/product/${updatedProduct.productId}")
                .request()
                .put(Entity.json(updatedProduct))
        // TODO - warning: the data is sourced from a dump in-memory hashMap so that I could work around the need for a DB.

        assertEquals(200, response.status)
        val result: Product = response.readEntity(object: GenericType<Product>() {})
        assertEquals(updatedProduct, result)
    }
}