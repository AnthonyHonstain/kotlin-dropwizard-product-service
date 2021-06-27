package honstain

import com.codahale.metrics.annotation.Timed
import honstain.api.Product
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
class ProduceResource {

    val products = mutableMapOf(
            1L to Product(1, "SKU-01", null, null),
            2L to Product(2, "SKU-02", null, null),
            3L to Product(3, "SKU-03", null, null),
    )

    @GET
    fun getAll(): List<Product> {
        return products.values.toList()
    }

    @GET
    @Timed
    @Path("/{productId}")
    fun getSingle(@PathParam("productId") productId: Long): Product {
        val result = products.getOrElse(productId, {
            throw NotFoundException()
        })
        return result
    }

    @POST
    @Timed
    fun create(product: Product): Product {
        products[product.productId] = product
        return product
    }

    @PUT
    @Timed
    @Path("/{productId}")
    fun update(@PathParam("productId") productId: Long, product: Product): Product {
        if (productId != product.productId) throw BadRequestException()

        products.getOrElse(productId, {
            throw NotFoundException()
        })
        products[product.productId] = product
        return product
    }
}