package br.davyfelix.dinofoods.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.davyfelix.dinofoods.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        auth = FirebaseAuth.getInstance()

        val btnReset = findViewById<Button>(R.id.btnResetPassword)
        val editEmail = findViewById<EditText>(R.id.editEmailReset)

        btnReset.setOnClickListener {
            val email = editEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                enviarEmailRecuperacao(email)
            } else {
                Toast.makeText(this, "Preencha o e-mail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarEmailRecuperacao(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "E-mail enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show()
                    finish() // Volta para a tela de login
                } else {
                    val erro = task.exception?.message ?: "Erro ao enviar e-mail"
                    Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                }
            }
    }
}