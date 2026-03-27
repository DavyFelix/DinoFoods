package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.activities.ProfileActivity
import br.davyfelix.dinofoods.fragments.FoodsFragments

class HomeActivity : AppCompatActivity() {

    // Declaramos para que os fragments possam acessar
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

// Deixa a barra de status 100% transparente
        window.statusBarColor = android.graphics.Color.TRANSPARENT

// Garante que os ícones da barra (hora, bateria) fiquem brancos
// Se o seu roxo for muito claro, mude para 'true' para ícones pretos
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        // Carrega o fragment de comida inicialmente
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FoodsFragments())
                .commit()
        }
        // Configura as ações do Menu Lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_ordes -> {
                    val intent = Intent(this, OrdesActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}