package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.fragments.FoodsFragments
import br.davyfelix.dinofoods.services.AppwriteService
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
        configurarBarraStatus()

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

    private fun configurarBarraStatus() {
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            // false = ícones brancos | true = ícones pretos
            isAppearanceLightStatusBars = false
        }
    }

    private fun configurarNavigation(navView: NavigationView) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ordes -> {
                    startActivity(Intent(this, OrdesActivity::class.java))
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

                    txtNome.text = "Olá, $nome!"
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