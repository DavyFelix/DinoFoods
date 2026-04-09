package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.R.*
import br.davyfelix.dinofoods.databinding.ActivityRegisterBinding
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)


        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltar.setOnClickListener {
            finish()
        }
        auth = FirebaseAuth.getInstance()

        binding.btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val nome = binding.editNome.text.toString()
        val email = binding.editEmail.text.toString()
        val senha = binding.editSenha.text.toString()

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Cria a conta no Firebase
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""

                    // 2. Salva os dados extras no Appwrite usando seu Service
                    lifecycleScope.launch {
                        try {
                            AppwriteService.salvarPerfilUsuario(userId, nome, email)

                            Toast.makeText(this@RegisterActivity,
                                getString(string.cadastro_salvos), Toast.LENGTH_SHORT).show()

                            // Ir para a LoginActivity ou Home
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } catch (e: Exception) {
                            // Log de erro caso o Appwrite falhe (ex: falta de permissão)
                            Toast.makeText(this@RegisterActivity,
                                getString(string.erro_appwrite, e.message), Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this,
                        getString(string.erro_firebase, task.exception?.message), Toast.LENGTH_LONG).show()
                }
            }
    }
}
