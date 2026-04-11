package br.davyfelix.dinofoods.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.activities.HomeActivity
import br.davyfelix.dinofoods.adapters.ProdutoAdapter
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.data.Produto
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FoodsFragments : Fragment() {

    private lateinit var adapter: ProdutoAdapter
    private lateinit var recyclerView: RecyclerView
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
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            carregarProdutos()
        } else {
            Log.e("FoodsFragment", "Usuário nulo ao carregar lista")
        }

        // 1. Inicialização das Views
        recyclerView = view.findViewById(R.id.rvProdutos)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabCarrinho)
        val etBusca = view.findViewById<EditText>(R.id.etBusca)
        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)
        val header = view.findViewById<LinearLayout>(R.id.header)

        // 2. Ajuste Edge-to-Edge (Padding da Status Bar)
        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBars.top)
            insets
        }

        // 3. Configuração do RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 4. Menu Lateral
        btnMenu?.setOnClickListener {
            (activity as? HomeActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
        }

        // 5. Lógica de Busca
        etBusca.addTextChangedListener { text ->
            val query = text.toString().lowercase()
            if (::adapter.isInitialized) {
                val listaFiltrada = if (query.isEmpty()) {
                    listaOriginal
                } else {
                    listaOriginal.filter {
                        it.productName.lowercase().contains(query) ||
                                it.description.lowercase().contains(query)
                    }
                }
                adapter.updateList(listaFiltrada)
            }
        }

        // 6. Botão Flutuante (Carrinho)
        fab.setOnClickListener {
            if (Carrinho.itens.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.carrinho_vazio), Toast.LENGTH_SHORT).show()
            } else {
                navegarParaFragment(CarrinhoFragment())
            }
        }

        // 7. Carregar dados do Backend
        carregarProdutos()
    }

    private fun carregarProdutos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AppwriteService.getProdutos()

                listaOriginal = response.documents.map { doc ->
                    val imageId = doc.data["imageId"]?.toString() ?: ""
                    Produto(
                        productName = doc.data["productName"].toString(),
                        description = doc.data["description"].toString(),
                        price = (doc.data["price"] as? Number)?.toDouble() ?: 0.0,
                        imagemID = if (imageId.isNotEmpty()) AppwriteService.getImageUrl(imageId) else ""
                    )
                }

                // Configura o Adapter com o callback de clique para detalhes
                adapter = ProdutoAdapter(listaOriginal) { produtoSelecionado ->
                    val bundle = Bundle().apply {
                        putSerializable("produto", produtoSelecionado)
                    }
                    val detalheFrag = DetailsFoodsFragment().apply {
                        arguments = bundle
                    }
                    navegarParaFragment(detalheFrag)
                }

                recyclerView.adapter = adapter

            } catch (e: Exception) {
                Log.e("DinoFoods", "Erro ao carregar produtos: ${e.message}", e)
                Toast.makeText(requireContext(), "Erro ao conectar com o servidor", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Função auxiliar para evitar repetição de código de transição
    private fun navegarParaFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}