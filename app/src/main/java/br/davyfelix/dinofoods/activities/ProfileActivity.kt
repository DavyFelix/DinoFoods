package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    // Variáveis globais para evitar NullPointerException
    private lateinit var layoutReal: View
    private lateinit var layoutSkeleton: com.facebook.shimmer.ShimmerFrameLayout
    private lateinit var imgProfile: ShapeableImageView
    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // 1. Inicialização de todas as Views
        layoutReal = findViewById(R.id.layoutReal)
        layoutSkeleton = findViewById(R.id.shimmer_view_container)
        imgProfile = findViewById(R.id.imgProfile)
        txtNome = findViewById(R.id.txtUserName)
        txtEmail = findViewById(R.id.txtBio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        configurarCliques()
    }

    override fun onResume() {
        super.onResume()
        // Chamamos aqui para atualizar sempre que o usuário voltar da edição
        carregarDadosPerfil()
    }

    private fun configurarCliques() {
        // Botão Voltar
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Editar Perfil
        findViewById<MaterialButton>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Meus Pedidos
        findViewById<View>(R.id.containerPedidos).setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
        findViewById<View>(R.id.containerEnderecos).setOnClickListener{
            startActivity(Intent(this, AddressActivity::class.java))
        }

        // Sair da Conta
        findViewById<View>(R.id.txtLogoutContainer).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun carregarDadosPerfil() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        // Inicia o Skeleton
        layoutReal.visibility = View.GONE
        layoutSkeleton.visibility = View.VISIBLE
        layoutSkeleton.startShimmer()

        lifecycleScope.launch {
            try {
                // Busca no Appwrite
                val documento = AppwriteService.getDatabase().getDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_USUARIOS,
                    documentId = currentUser.uid
                )

                val nome = documento.data["nome"]?.toString() ?: "Explorador"
                val email = documento.data["email"]?.toString() ?: currentUser.email
                val fotoId = documento.data["fotoCapa"]?.toString()

                // Atualiza Textos
                txtNome.text = getString(R.string.ol, nome)
                txtEmail.text = email

                // Carrega Imagem
                if (!fotoId.isNullOrEmpty()) {
                    val url = AppwriteService.getImageUrl(fotoId)
                    Glide.with(this@ProfileActivity)
                        .load(url)
                        .placeholder(R.drawable.ic_launcher_background)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Garante que pegue a foto nova
                        .skipMemoryCache(true)
                        .into(imgProfile)
                }

                // Desliga Skeleton e mostra layout real
                layoutSkeleton.stopShimmer()
                layoutSkeleton.visibility = View.GONE
                layoutReal.visibility = View.VISIBLE

            } catch (e: Exception) {
                Log.e("Appwrite", "Erro: ${e.message}")
                // Fallback em caso de erro
                layoutSkeleton.stopShimmer()
                layoutSkeleton.visibility = View.GONE
                layoutReal.visibility = View.VISIBLE
                txtNome.text = "Olá, Explorador"
                txtEmail.text = currentUser.email
            }
        }
    }
}