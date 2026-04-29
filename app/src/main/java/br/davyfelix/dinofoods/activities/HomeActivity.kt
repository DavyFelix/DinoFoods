package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.fragments.FoodsFragments
import br.davyfelix.dinofoods.services.AppwriteService
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class HomeActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 1. Inicialização básica
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        // 2. Fragment Inicial
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FoodsFragments())
                .commit()
        }

        // 3. Configurações de navegação fixa
        configurarNavigation()
    }

    // Método fundamental: executa toda vez que o usuário volta para esta tela
    override fun onResume() {
        super.onResume()
        // Atualiza os dados do header (nome, email, foto e status de admin)
        configurarHeader()
    }

    private fun configurarNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ordes -> startActivity(Intent(this, OrdersActivity::class.java))
                R.id.nav_perfil -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_adm -> startActivity(Intent(this, AdminActivity::class.java))
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
    // Na HomeActivity
    fun abrirMenu() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun configurarHeader() {
        val headerView = navView.getHeaderView(0)
        val txtNome = headerView.findViewById<TextView>(R.id.txtNome)
        val txtEmail = headerView.findViewById<TextView>(R.id.txtEmail)
        val imgPerfil = headerView.findViewById<ImageView>(R.id.imgPerfilHeader)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            lifecycleScope.launch {
                try {
                    val documento = AppwriteService.getDatabase().getDocument(
                        databaseId = AppwriteService.DATABASE_ID,
                        collectionId = AppwriteService.COLLECTION_USUARIOS,
                        documentId = currentUser.uid
                    )

                    // Atualiza visibilidade do menu Admin
                    val isAdmin = documento.data["isAdmin"] as? Boolean ?: false
                    navView.menu.findItem(R.id.nav_adm)?.isVisible = isAdmin

                    // Atualiza textos
                    val nome = documento.data["nome"]?.toString() ?: "Explorador"
                    val email = documento.data["email"]?.toString() ?: currentUser.email
                    txtNome.text = "Olá, $nome!"
                    txtEmail.text = email

                    // Atualiza foto de perfil
                    val fotoId = documento.data["fotoCapa"]?.toString()
                    if (!fotoId.isNullOrEmpty()) {
                        val urlFinal = AppwriteService.getImageUrl(fotoId)
                        imgPerfil.load(urlFinal) {
                            crossfade(true)
                            placeholder(R.drawable.bg_skeleton_circle)
                            error(R.drawable.bg_skeleton_circle)
                            transformations(CircleCropTransformation())
                        }
                    } else {
                        imgPerfil.setImageResource(R.drawable.bg_skeleton_circle)
                    }

                } catch (e: Exception) {
                    Log.e("Appwrite", "Erro ao atualizar Home: ${e.message}")
                    // Fallback para dados básicos do Firebase em caso de erro de rede
                    txtNome.text = "Olá!"
                    txtEmail.text = currentUser.email
                }
            }
        }
    }
}