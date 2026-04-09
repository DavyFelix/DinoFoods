package br.davyfelix.dinofoods.activities

import Orders
import OrdersAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
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
import com.google.firebase.auth.FirebaseAuth
import io.appwrite.Query
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {

    private lateinit var rvPedidos: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ordes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvPedidos = findViewById(R.id.rvPedidos)
        progressBar = findViewById(R.id.progressBar)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        rvPedidos.layoutManager = LinearLayoutManager(this)

        carregarPedidosDoAppwrite()
    }

    private fun carregarPedidosDoAppwrite() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (userEmail == null) {
            Toast.makeText(this, getString(R.string.vautenticado), Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // 1. Busca os documentos filtrando pelo e-mail
                val response = AppwriteService.getDatabase().listDocuments(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_PEDIDOS,
                    queries = listOf(
                        Query.equal("email", userEmail)
                    )
                )

                // 2. Converte os documentos do Appwrite para a lista de objetos Pedido
                val listaDePedidos = response.documents.map { doc ->
                    Orders(
                        id = doc.id,
                        status = doc.data["status"].toString(),
                        timestamp = (doc.data["timestamp"] as? Number)?.toLong() ?: 0L,
                        itens = doc.data["itens"].toString()
                    )
                }

                // 3. Atualiza a UI
                if (listaDePedidos.isEmpty()) {
                    Toast.makeText(this@OrdersActivity,
                        getString(R.string.sem_pedidos), Toast.LENGTH_SHORT).show()
                } else {

                    val adapter = OrdersAdapter(listaDePedidos) { pedidoSelecionado ->
                        // 1. Instancia o fragmento de detalhes
                        val fragment = br.davyfelix.dinofoods.fragments.OrderDetailsFragment()

                        // 2. Prepara os dados para enviar (O Bundle)
                        val bundle = Bundle()
                        bundle.putSerializable("pedido", pedidoSelecionado) // A classe Orders deve ser Serializable!
                        fragment.arguments = bundle

                        // 3. Abre o fragmento
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main, fragment) // Substitui a lista pelo detalhe
                            .addToBackStack(null)         // Permite voltar para a lista
                            .commit()
                    }
                    rvPedidos.adapter = adapter
                }

            } catch (e: Exception) {
                Log.e("ORDES_ERROR", "Erro: ${e.message}")
                Toast.makeText(this@OrdersActivity,
                    getString(R.string.erro_pedidos), Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}