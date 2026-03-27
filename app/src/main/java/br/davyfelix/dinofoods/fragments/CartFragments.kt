package br.davyfelix.dinofoods.fragments

import android.os.Build.ID
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
                Toast.makeText(requireContext(), "Seu carrinho ficou vazio!", Toast.LENGTH_SHORT).show()
                // Opcional: fechar a tela se o carrinho esvaziar
                // parentFragmentManager.popBackStack()
            }
        }

        rvCarrinho.adapter = adapter

        atualizarTotal(tvTotal)
        btnFinalizar.setOnClickListener {
            if (Carrinho.itens.isNotEmpty()) {

                // Em vez de criar um novo, usamos o que já está no seu Service
                val database = AppwriteService.getDatabase()

                // Se você ainda não tem a lógica de pegar o e-mail do usuário logado:
                val email = "cliente@teste.com"

                // Transforma a lista de itens em JSON (certifique-se de ter a lib Gson no build.gradle)
                val itensJson = Gson().toJson(Carrinho.itens)

                val pedidoData = mapOf(
                    "email" to email,
                    "status" to "pendente",
                    "timestamp" to System.currentTimeMillis(),
                    "itens" to itensJson
                )

                lifecycleScope.launch {
                    try {
                        database.createDocument(
                            databaseId = AppwriteService.DATABASE_ID,
                            collectionId = AppwriteService.COLLECTION_PEDIDOS,
                            documentId = io.appwrite.ID.unique(), // Aqui usamos o ID do Appwrite
                            data = pedidoData
                        )

                        Toast.makeText(requireContext(), "Pedido enviado! 🍽️", Toast.LENGTH_LONG).show()
                        Carrinho.itens.clear()
                        parentFragmentManager.popBackStack()

                    } catch (e: Exception) {
                        Log.e("APPWRITE_ERROR", "Erro: ${e.message}")
                        Toast.makeText(requireContext(), "Erro ao enviar pedido!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Adicione itens primeiro!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarTotal(tvTotal: TextView) {
        val total = Carrinho.itens.sumOf { it.price }
        tvTotal.text = "Total: R$ %.2f".format(total)
    }
}

