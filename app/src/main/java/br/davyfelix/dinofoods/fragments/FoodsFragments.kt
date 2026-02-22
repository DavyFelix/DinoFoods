package br.davyfelix.dinofoods.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.adapters.ProdutoAdapter
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.model.Produto

class FoodsFragments : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_foods_fragments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProdutos)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabCarrinho)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProdutoAdapter(listaDeProdutos())

        fab.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Itens no carrinho: ${Carrinho.itens.size}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun listaDeProdutos(): List<Produto> {
        return listOf(
            Produto("Hambúrguer Especial", "Carne 180g + cheddar", 29.90),
            Produto("Pizza Calabresa", "Molho artesanal + mussarela", 39.90),
            Produto("Refrigerante", "Lata 350ml", 6.00)
        )
    }
}