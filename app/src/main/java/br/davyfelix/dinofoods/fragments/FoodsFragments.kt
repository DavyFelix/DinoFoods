package br.davyfelix.dinofoods.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.activities.HomeActivity
import br.davyfelix.dinofoods.adapters.ProdutoAdapter
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.data.Produto
import br.davyfelix.dinofoods.services.AppwriteService
import kotlinx.coroutines.launch

class FoodsFragments : Fragment() {

    private lateinit var adapter: ProdutoAdapter
    private var listaOriginal: List<Produto> = listOf()

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
        val etBusca = view.findViewById<EditText>(R.id.etBusca)
        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)
        val header = view.findViewById<LinearLayout>(R.id.header) // Pegamos o ID do seu Header Roxo

        // --- AJUSTE PARA OPÇÃO 1 (EDGE-TO-EDGE) ---
        // Isso faz o header respeitar a "zona segura" da barra de status automaticamente
        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBars.top)
            insets
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        btnMenu?.setOnClickListener {
            (activity as? HomeActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
        }

        etBusca.addTextChangedListener { text ->
            val query = text.toString().lowercase()
            val listaFiltrada = listaOriginal.filter {
                it.productName.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
            }
            if (::adapter.isInitialized) {
                adapter.updateList(listaFiltrada)
            }
        }

        carregarProdutos(recyclerView)

        fab.setOnClickListener {
            if (Carrinho.itens.isEmpty()) {
                Toast.makeText(requireContext(), "O carrinho está vazio!", Toast.LENGTH_SHORT).show()
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, CarrinhoFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun carregarProdutos(recyclerView: RecyclerView) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AppwriteService.getProdutos()
                listaOriginal = response.documents.map { doc ->
                    val testaImagem = doc.data["imageId"]?.toString() ?: ""
                    Produto(
                        productName = doc.data["productName"].toString(),
                        description = doc.data["description"].toString(),
                        price = (doc.data["price"] as Number).toDouble(),
                        imagemID = if (testaImagem.isNotEmpty()) AppwriteService.getImageUrl(testaImagem) else ""
                    )
                }

                adapter = ProdutoAdapter(listaOriginal) { produto ->
                    Log.d("DinoFoods", "Produto adicionado: ${produto.productName}")
                }
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                Log.e("DinoFoods", "Erro ao carregar produtos", e)
                Toast.makeText(requireContext(), "Erro ao carregar produtos", Toast.LENGTH_LONG).show()
            }
        }
    }
}