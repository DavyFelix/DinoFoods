package br.davyfelix.dinofoods.data

data class Produto(
    val productName: String,
    val description: String,
    val price: Double,
    val imagemID: String? = null
)