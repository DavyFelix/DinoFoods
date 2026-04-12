package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
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

class HomeActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configurações de UI e Edge-to-Edge
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 2. Inicialização de componentes
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        // 3. Carregar dados do usuário no Header do Menu
        configurarHeader(navView)

        // 4. Fragment Inicial (Lista de Comidas)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FoodsFragments())
                .commit()
        }

        // 5. Configuração do Menu Lateral (Drawer)
        configurarNavigation(navView)
    }

    private fun configurarNavigation(navView: NavigationView) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ordes -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
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

    private fun configurarHeader(navView: NavigationView) {
        val headerView = navView.getHeaderView(0)
        val txtNome = headerView.findViewById<TextView>(R.id.txtNome)
        val txtEmail = headerView.findViewById<TextView>(R.id.txtEmail)
        val imgPerfil = headerView.findViewById<ImageView>(R.id.imgPerfilHeader) // Certifique-se que o ID no XML é este

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            lifecycleScope.launch {
                try {
                    val documento = AppwriteService.getDatabase().getDocument(
                        databaseId = AppwriteService.DATABASE_ID,
                        collectionId = AppwriteService.COLLECTION_USUARIOS,
                        documentId = currentUser.uid
                    )

                    val nome = documento.data["nome"]?.toString() ?: "Explorador"
                    val email = documento.data["email"]?.toString() ?: currentUser.email

                    // 1. Pegamos o ID da imagem (ex: 69dad7...)
                    val fotoId = documento.data["fotoCapa"]?.toString()

                    txtNome.text = "Olá, $nome!"
                    txtEmail.text = email

                    // 2. Se o fotoId não for nulo nem vazio, carregamos a imagem
                    if (!fotoId.isNullOrEmpty()) {
                        // Usamos sua função do AppwriteService para gerar a URL completa
                        val urlFinal = AppwriteService.getImageUrl(fotoId)

                        imgPerfil.load(urlFinal) {
                            crossfade(true)
                            placeholder(R.drawable.bg_skeleton_circle) // Imagem padrão enquanto baixa
                            error(R.drawable.bg_skeleton_circle)       // Imagem caso dê erro
                            transformations(CircleCropTransformation())   // Deixa a foto redonda
                        }
                    }

                } catch (e: Exception) {
                    Log.e("Appwrite", "Erro ao carregar perfil: ${e.message}")
                    txtNome.text = "Olá, Explorador!"
                    txtEmail.text = currentUser.email
                    // Opcional: colocar uma imagem padrão no erro
                    imgPerfil.setImageResource(R.drawable.bg_skeleton_circle)
                }
            }
        }
    }
}