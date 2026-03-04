package br.davyfelix.dinofoods.model

data class Produto(
    val productName: String,
    val description: String,
    val price: Double,
    val imagemUrl: String? = null
)