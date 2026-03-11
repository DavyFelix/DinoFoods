package br.davyfelix.dinofoods.data

import br.davyfelix.dinofoods.data.Produto

object Carrinho {

    val itens = mutableListOf<Produto>()

    fun adicionar(produto: Produto) {
        itens.add(produto)
    }

    fun total(): Double {
        return itens.sumOf { it.price }
    }
}