package honstain.api

data class Product (
    var productId: Long,
    var sku: String,
    var barcode: String?,
    var taxCode: String?,
)