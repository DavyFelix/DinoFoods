package br.davyfelix.dinofoods.fragments

import android.content.Intent
import android.os.Build.ID
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.adapters.CarrinhoAdapter // Importamos o novo Adapter
import br.davyfelix.dinofoods.data.Carrinho
import com.google.gson.Gson
import kotlinx.coroutines.launch
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.firebase.auth.FirebaseAuth

class CarrinhoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Certifique-se de que o nome do XML aqui seja o mesmo que você criou (fragment_carrinho)
        return inflater.inflate(R.layout.fragment_cart_fragments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCarrinho = view.findViewById<RecyclerView>(R.id.rvCarrinho)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
        val btnFinalizar = view.findViewById<Button>(R.id.btnFinalizar)
        val btnBackCart = view.findViewById<ImageButton>(R.id.btnBackCart)

        btnBackCart.setOnClickListener { parentFragmentManager.popBackStack() }

        rvCarrinho.layoutManager = LinearLayoutManager(requireContext())

        // Usamos o CarrinhoAdapter com a lógica de remoção
        val adapter = CarrinhoAdapter(Carrinho.itens) { produtoRemovido ->
            // 1. Remove da lista global
            Carrinho.itens.remove(produtoRemovido)

            // 2. Notifica o adapter que a lista mudou para atualizar a tela
            rvCarrinho.adapter?.notifyDataSetChanged()

            // 3. Recalcula o valor total
            atualizarTotal(tvTotal)

            if (Carrinho.itens.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.seu_carrinho_ficou_vazio), Toast.LENGTH_SHORT).show()
                // Opcional: fechar a tela se o carrinho esvaziar
                // parentFragmentManager.popBackStack()
            }
        }

        rvCarrinho.adapter = adapter

        atualizarTotal(tvTotal)
        btnFinalizar.setOnClickListener {
            if (Carrinho.itens.isNotEmpty()) {

                // 1. Pegar a instância do Firebase Auth
                val userFirebase = FirebaseAuth.getInstance().currentUser

                // 2. Verificar se o usuário está realmente logado
                if (userFirebase == null) {
                    Toast.makeText(requireContext(),
                        getString(R.string.vautenticado), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 3. Pegar o email do Firebase
                val email = userFirebase.email ?: "email_desconhecido@teste.com"

                val database = AppwriteService.getDatabase()
                val itensJson = Gson().toJson(Carrinho.itens)

                val pedidoData = mapOf(
                    "email" to email,
                    "status" to "pendente",
                    "timestamp" to System.currentTimeMillis(),
                    "itens" to itensJson
                    // Se você precisar do ID do usuário do Appwrite futuramente,
                    // precisaria fazer um "listDocuments" na sua tabela de usuários antes.
                )

                lifecycleScope.launch {
                    try {
                        database.createDocument(
                            databaseId = AppwriteService.DATABASE_ID,
                            collectionId = AppwriteService.COLLECTION_PEDIDOS,
                            documentId = io.appwrite.ID.unique(),
                            data = pedidoData
                        )

                        Toast.makeText(requireContext(),
                            getString(R.string.pedido_enviado), Toast.LENGTH_LONG).show()
                        Carrinho.itens.clear()
                        parentFragmentManager.popBackStack()

                    } catch (e: Exception) {
                        Log.e("APPWRITE_ERROR", "Erro: ${e.message}")
                        Toast.makeText(requireContext(),
                            getString(R.string.erro_ao_enviar_pedido), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.adicione_itens_primeiro), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarTotal(tvTotal: TextView) {
        val total = Carrinho.itens.sumOf { it.price }
        tvTotal.text = getString(R.string.total_r_2f).format(total)
    }
}

