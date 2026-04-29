package br.davyfelix.dinofoods.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.adapters.ProdutoAdapter
import br.davyfelix.dinofoods.data.Produto
import br.davyfelix.dinofoods.services.AppwriteService
import kotlinx.coroutines.launch

class DeleteProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProdutoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_products)

        // Configuração de Padding para StatusBar/NavBar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar botão de voltar
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.rvDeletarProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa o adapter vazio primeiro para evitar erros de inicialização
        adapter = ProdutoAdapter(mutableListOf(), true) { produto ->
            confirmarExclusao(produto)
        }
        recyclerView.adapter = adapter

        carregarProdutosParaDeletar()
    }

    private fun carregarProdutosParaDeletar() {
        lifecycleScope.launch {
            try {
                val response = AppwriteService.getProdutos()
                val lista = response.documents.map { doc ->
                    val imageId = doc.data["imageId"]?.toString() ?: ""
                    Produto(
                        id = doc.id,
                        productName = doc.data["productName"].toString(),
                        description = doc.data["description"].toString(),
                        price = (doc.data["price"] as? Number)?.toDouble() ?: 0.0,
                        imagemID = if (imageId.isNotEmpty()) AppwriteService.getImageUrl(imageId) else ""
                    )
                }

                adapter.updateList(lista)

            } catch (e: Exception) {
                Log.e("DeleteActivity", "Erro: ${e.message}")
                Toast.makeText(this@DeleteProductsActivity, "Erro ao carregar lista", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarExclusao(produto: Produto) {
        lifecycleScope.launch {
            try {
                produto.id?.let { id ->
                    AppwriteService.deletarProduto(id)
                    Toast.makeText(this@DeleteProductsActivity, "Produto removido!", Toast.LENGTH_SHORT).show()

                    // Em vez de recarregar tudo, poderíamos apenas remover da lista do adapter
                    // mas carregar de novo garante que o banco e a tela estão iguais
                    carregarProdutosParaDeletar()
                } ?: run {
                    Toast.makeText(this@DeleteProductsActivity, "ID do produto inválido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DeleteActivity", "Erro ao excluir: ${e.message}")
                Toast.makeText(this@DeleteProductsActivity, "Erro ao excluir", Toast.LENGTH_SHORT).show()
            }
        }
    }
}