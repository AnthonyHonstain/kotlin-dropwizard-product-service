package honstain

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
    @Path("/{productId}")
    fun helloWorld(@PathParam("productId") productId:String): Product {
        val result = products.getOrElse(productId.toLong(), {
            throw BadRequestException()
        })
        return result
    }

    // TODO - Still porting https://bitbucket.org/honstain/javalin-product-service/src/master/src/main/kotlin/com/mycompany/product/ProductController.kt
}