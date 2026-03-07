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

        // Chamada assíncrona para buscar os produtos no Appwrite
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AppwriteService.getProdutos()

                val listaVindaDoAppwrite = response.documents.map { doc ->
                    Produto(
                        productName = doc.data["productName"].toString(),
                        description = doc.data["description"].toString(),
                        price = (doc.data["price"] as Number).toDouble(),
                        imagemUrl = AppwriteService.getImageUrl(doc.data["imagemId"].toString())
                    )
                }

                // Configura o Adapter passando o callback de clique
                recyclerView.adapter = ProdutoAdapter(listaVindaDoAppwrite) { produto ->
                    // Esta lógica roda dentro do Adapter quando o botão 'Adicionar' é clicado
                    // O Carrinho.adicionar(produto) já acontece no Adapter,
                    // aqui você pode adicionar efeitos extras na UI do Fragment.
                    Log.d("DinoFoods", "Produto adicionado: ${produto.productName}")
                }

            } catch (e: Exception) {
                Log.e("DinoFoods", "Erro ao carregar produtos", e)
                Toast.makeText(requireContext(), "Erro ao carregar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Configuração do FAB para abrir a tela do Carrinho
        fab.setOnClickListener {
            if (Carrinho.itens.isEmpty()) {
                Toast.makeText(requireContext(), "O carrinho está vazio!", Toast.LENGTH_SHORT).show()
            } else {
                // Esta é a lógica que faz a troca de tela (Fragment Transaction)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, CarrinhoFragment()) // 'fragment_container' deve ser o ID do FrameLayout/FragmentContainerView da sua MainActivity
                    .addToBackStack(null) // Permite que o usuário volte ao clicar no botão "Voltar" do celular
                    .commit()
            }
        }
    }


}