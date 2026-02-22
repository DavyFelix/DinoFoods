package br.davyfelix.dinofoods.model

data class Produto(
    val nome: String,
    val descricao: String,
    val preco: Double = 0.0
)