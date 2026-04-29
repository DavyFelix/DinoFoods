package br.davyfelix.dinofoods.data

data class Produto(
    val id: String? = null,
    val productName: String,
    val description: String,
    val price: Double,
    val imagemID: String
) : java.io.Serializable