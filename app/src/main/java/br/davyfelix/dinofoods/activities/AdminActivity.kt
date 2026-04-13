package br.davyfelix.dinofoods.activities

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import kotlinx.coroutines.launch
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class AdminActivity : AppCompatActivity() {


    private lateinit var analytics: FirebaseAnalytics

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var etCategory: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCrash: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteService.init(applicationContext)

        // 📊 Inicialização do Firebase Analytics
        analytics = FirebaseAnalytics.getInstance(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        etName = findViewById(R.id.etProductName)
        etPrice = findViewById(R.id.etPrice)
        etStock = findViewById(R.id.etStock)
        etCategory = findViewById(R.id.etCategory)
        btnAdd = findViewById(R.id.btnAddProduct)
        btnCrash = findViewById(R.id.btnTestCrash)

        btnAdd.setOnClickListener {
            addProduct()
        }

        btnCrash.setOnClickListener {
            // Log de evento de clique no crash (antes de estourar)
            analytics.logEvent("debug_crash_clicked") {
                param("screen", "AdminActivity")
            }
            throw RuntimeException("Teste de crash manual 🚨")
        }
    }

    private fun addProduct() {
        val name = etName.text.toString()
        val priceText = etPrice.text.toString()
        val stockText = etStock.text.toString()
        val category = etCategory.text.toString()

        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDouble()
        val stock = stockText.toInt()

        val data = mapOf(
            "productName" to name,
            "price" to price,
            "stockQuantity" to stock,
            "category" to category
        )

        lifecycleScope.launch {
            try {
                AppwriteService.getDatabase().createDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_PRODUTOS,
                    documentId = io.appwrite.ID.unique(),
                    data = data
                )

                // 📈 Log de sucesso no Analytics
                analytics.logEvent("product_added") {
                    param("product_name", name)
                    param("category", category)
                    param("price", price)
                }

                Toast.makeText(this@AdminActivity, "Produto salvo!", Toast.LENGTH_SHORT).show()
                clearFields()

            } catch (e: Exception) {
                // ⚠️ Log de erro no Analytics
                analytics.logEvent("product_add_error") {
                    param("error_message", e.message ?: "Unknown error")
                }
                Toast.makeText(this@AdminActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearFields() {
        etName.text.clear()
        etPrice.text.clear()
        etStock.text.clear()
        etCategory.text.clear()
    }
}