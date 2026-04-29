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

        // 1. Inicialização das Views
        recyclerView = view.findViewById(R.id.rvProdutos)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabCarrinho)
        val etBusca = view.findViewById<EditText>(R.id.etBusca)
        val btnMenu = view.findViewById<ImageView>(R.id.btnMenu)
        val header = view.findViewById<LinearLayout>(R.id.header)

        // 2. Ajuste Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBars.top)
            insets
        }

        // 3. Configuração do RecyclerView (Instanciar adapter vazio inicialmente evita erros na busca)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProdutoAdapter(listOf()) { produtoSelecionado ->
            val bundle = Bundle().apply {
                putSerializable("produto", produtoSelecionado)
            }
            val detalheFrag = DetailsFoodsFragment().apply {
                arguments = bundle
            }
            navegarParaFragment(detalheFrag)
        }
        recyclerView.adapter = adapter

        // 4. Menu Lateral
        btnMenu?.setOnClickListener {
            (activity as? HomeActivity)?.abrirMenu()
        }

        // 5. Lógica de Busca (Agora segura, pois o adapter já foi iniciado)
        etBusca.addTextChangedListener { text ->
            val query = text.toString().lowercase()
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

        // 6. Botão Flutuante (Carrinho)
        fab.setOnClickListener {
            if (Carrinho.itens.isEmpty()) {
                Toast.makeText(requireContext(), R.string.carrinho_vazio, Toast.LENGTH_SHORT).show()
            } else {
                navegarParaFragment(CarrinhoFragment())
            }
        }

        // 7. Única chamada para carregar produtos
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            carregarProdutos()
        }
    }

    private fun carregarProdutos() {
        // Usamos viewLifecycleOwner para garantir que a coroutine pare se a view sumir
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

                // Apenas atualiza a lista do adapter que já existe
                adapter.updateList(listaOriginal)

            } catch (e: Exception) {
                Log.e("DinoFoods", "Erro ao carregar produtos: ${e.message}")
                if (isAdded) { // Verifica se o fragmento ainda está na tela
                    Toast.makeText(requireContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navegarParaFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}