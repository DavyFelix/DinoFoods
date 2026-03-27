package br.davyfelix.dinofoods.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import kotlinx.coroutines.launch

class OrdesActivity : AppCompatActivity() {

    private lateinit var rvPedidos: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ordes)

        // Configuração das bordas do sistema (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializar os componentes da tela
        rvPedidos = findViewById(R.id.rvPedidos) // Certifique-se que o ID no XML é este
        progressBar = findViewById(R.id.progressBar)

        rvPedidos.layoutManager = LinearLayoutManager(this)

        // 2. Chamar a função para carregar os dados
        carregarPedidosDoAppwrite()
    }

    private fun carregarPedidosDoAppwrite() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Busca os documentos na coleção de pedidos
                val response = AppwriteService.getDatabase().listDocuments(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_PEDIDOS
                )

                val listaDePedidos = response.documents

                if (listaDePedidos.isEmpty()) {
                    Toast.makeText(this@OrdesActivity, "Você ainda não tem pedidos.", Toast.LENGTH_SHORT).show()
                } else {
                    // 3. Aqui você vai configurar o seu Adapter
                    // Exemplo: rvPedidos.adapter = PedidosAdapter(listaDePedidos)
                    Log.d("ORDES_APPWRITE", "Pedidos encontrados: ${listaDePedidos.size}")
                }

            } catch (e: Exception) {
                Log.e("ORDES_ERROR", "Erro ao buscar pedidos: ${e.message}")
                Toast.makeText(this@OrdesActivity, "Erro ao carregar pedidos", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}