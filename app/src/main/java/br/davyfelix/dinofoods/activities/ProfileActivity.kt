package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.davyfelix.dinofoods.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

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
            Toast.makeText(this, "Abrir edição de perfil", Toast.LENGTH_SHORT).show()
            // Aqui você faria o Intent para a tela de edição
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
}