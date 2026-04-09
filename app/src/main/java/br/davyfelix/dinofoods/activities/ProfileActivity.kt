package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.jvm.java

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita o modo tela cheia (Edge-to-Edge)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        profile()

        configurarCliques()
    }

    private fun configurarCliques() {

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botão Editar Perfil
        val btnEdit = findViewById<MaterialButton>(R.id.btnEditProfile)
        btnEdit.setOnClickListener {
        }
        // Opção Pedidos
        val layoutPedidos = findViewById<android.view.View>(R.id.containerPedidos)
        layoutPedidos.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        // Opção Sair
        val txtLogout = findViewById<TextView>(R.id.txtLogout)
        txtLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    private fun profile() {
        val txtNome = findViewById<TextView>(R.id.txtUserName)
        val txtEmail = findViewById<TextView>(R.id.txtBio)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            lifecycleScope.launch {
                try {
                    // Busca dados no Appwrite usando o UID do Firebase
                    val documento = AppwriteService.getDatabase().getDocument(
                        databaseId = AppwriteService.DATABASE_ID,
                        collectionId = AppwriteService.COLLECTION_USUARIOS,
                        documentId = currentUser.uid
                    )

                    val nome = documento.data["nome"]?.toString() ?: "Explorador"
                    val email = documento.data["email"]?.toString() ?: currentUser.email

                    txtNome.text = getString(R.string.ol, nome)
                    txtEmail.text = email

                } catch (e: Exception) {
                    Log.e("Appwrite", "Erro ao carregar perfil: ${e.message}")
                    // Fallback para dados básicos do Firebase em caso de erro de rede
                    txtNome.text = "Olá, Explorador!"
                    txtEmail.text = currentUser.email
                }
            }
        }
    }
}