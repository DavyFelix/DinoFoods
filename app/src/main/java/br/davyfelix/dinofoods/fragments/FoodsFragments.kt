package br.davyfelix.dinofoods.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.adapters.ProdutoAdapter
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.model.Produto
import br.davyfelix.dinofoods.services.AppwriteService
import kotlinx.coroutines.launch

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

        // Chamada assíncrona para o Appwrite
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AppwriteService.getProdutos()

                // Mapeia os documentos para a lista de objetos Produto
                val listaVindaDoAppwrite = response.documents.map { doc ->
                    Produto(
                        productName = doc.data["productName"].toString(),
                        description = doc.data["description"].toString(),
                        price = (doc.data["price"] as Number).toDouble(),
                        imagemUrl = AppwriteService.getImageUrl(doc.data["imagemId"].toString())
                    )
                }

                // Atualiza o adapter com os dados reais
                recyclerView.adapter = ProdutoAdapter(listaVindaDoAppwrite)

            } catch (e: Exception) {
                // Se der erro, agora saberemos o motivo real além do 'cancelled'
                Log.e("DinoFoods", "Erro detalhado: ${e.printStackTrace()}")
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_LONG).show()
        }
        }

        fab.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Itens no carrinho: ${Carrinho.itens.size}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}