package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Firebase manualmente
        FirebaseApp.initializeApp(this)
        AppwriteService.init(this)

        // Vai para Login
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
