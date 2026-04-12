package br.davyfelix.dinofoods.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import br.davyfelix.dinofoods.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseService : FirebaseMessagingService() {

    // 1. Quando o Firebase gera um novo token, enviamos para o Appwrite
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Novo Token Gerado: $token")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            salvarTokenNoAppwrite(currentUser.uid, token)
        }
    }

    // Função para salvar o token no seu banco de dados Appwrite (campo fcmToken)
    private fun salvarTokenNoAppwrite(uid: String, token: String) {
        // Usamos GlobalScope ou um Scope customizado pois o Service não tem lifecycleScope
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppwriteService.getDatabase().updateDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_USUARIOS,
                    documentId = uid,
                    data = mapOf("fcmToken" to token)
                )
                Log.d("Appwrite", "Token atualizado com sucesso!")
            } catch (e: Exception) {
                Log.e("Appwrite", "Erro ao salvar token: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Se a mensagem vier do Appwrite, ela pode vir dentro do 'data' ou 'notification'
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "DinoFoods"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // CRIAR CANAL (Obrigatório para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notificações DinoFoods", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}