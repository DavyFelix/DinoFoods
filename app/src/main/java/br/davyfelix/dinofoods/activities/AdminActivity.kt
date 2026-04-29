package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import kotlinx.coroutines.launch
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class AdminActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val cardAdd = findViewById<CardView>(R.id.cardAddProduct)
        val cardOrders = findViewById<CardView>(R.id.cardViewOrders)
        val cardCrash = findViewById<CardView>(R.id.cardCrash)
        val cardDel = findViewById<CardView>(R.id.cardDel)
        val btnBackCart = findViewById<ImageButton>(R.id.btnBackCart)
        btnBackCart.setOnClickListener {
            finish()
        }

        cardAdd.setOnClickListener {
            // Abre a tela que contém o formulário que você já criou
            val intent = Intent(this, AddProductsActivity::class.java)
            startActivity(intent)
        }

        cardOrders.setOnClickListener {
            // Abre a tela de listagem
            Toast.makeText(this, "Abrindo Pedidos...", Toast.LENGTH_SHORT).show()
        }

        cardCrash.setOnClickListener {
            throw RuntimeException("Crash de teste 🚨")
        }
        cardDel.setOnClickListener {
            val intent = Intent(this, DeleteProductsActivity::class.java)
            startActivity(intent)
        }
    }
}