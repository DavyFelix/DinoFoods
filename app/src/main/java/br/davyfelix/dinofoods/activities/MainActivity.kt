package br.davyfelix.dinofoods.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        AppwriteService.init(applicationContext)

        // 1. Inicializa serviços
        FirebaseApp.initializeApp(this)
        AppwriteService.init(this)

        // 2. Pede permissão (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                // Não dê finish() aqui se quiser esperar a resposta,
                // mas para o fluxo de login, pode seguir.
            }
        }

        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }


}
