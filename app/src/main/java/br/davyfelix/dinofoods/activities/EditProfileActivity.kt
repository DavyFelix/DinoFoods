package br.davyfelix.dinofoods.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.services.AppwriteService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import io.appwrite.models.InputFile
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var imgProfile: ShapeableImageView
    private var imageUri: Uri? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // 1. Lançador para escolher a foto (Photo Picker moderno)
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            imgProfile.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        etNome = findViewById(R.id.etEditNome)
        imgProfile = findViewById(R.id.imgEditProfile)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarPerfil)
        val btnBack = findViewById<ImageButton>(R.id.btnBackEdit)
        val fabCamera = findViewById<FloatingActionButton>(R.id.fabEditPhoto)

        carregarDadosAtuais()

        btnBack.setOnClickListener { finish() }

        // 2. Clique para abrir a galeria
        fabCamera.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSalvar.setOnClickListener {
            val novoNome = etNome.text.toString()
            if (novoNome.isNotEmpty()) {
                salvarAlteracoes(novoNome)
            } else {
                Toast.makeText(this, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarAlteracoes(nome: String) {
        lifecycleScope.launch {
            try {
                var fotoId: String? = null

                // 3. Se o usuário escolheu uma foto nova, faz o upload primeiro
                imageUri?.let { uri ->
                    fotoId = fazerUploadFoto(uri)
                }

                // 4. Monta os dados para atualizar no Database
                val dados = mutableMapOf<String, Any>("nome" to nome)
                fotoId?.let { dados["fotoCapa"] = it } // Só atualiza a foto se houver uma nova

                AppwriteService.getDatabase().updateDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_USUARIOS,
                    documentId = currentUser!!.uid,
                    data = dados
                )

                Toast.makeText(this@EditProfileActivity, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Log.e("EDIT_PROFILE", "Erro: ${e.message}")
                Toast.makeText(this@EditProfileActivity, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 5. Função para enviar a foto ao Storage do Appwrite
    private suspend fun fazerUploadFoto(uri: Uri): String {
        // Criar um arquivo temporário para enviar ao Appwrite
        val file = File(cacheDir, "temp_profile_image.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        // Envia para o Bucket de fotos (Certifique-se de ter o BUCKET_ID no seu AppwriteService)
        val response = AppwriteService.getStorage().createFile(
            bucketId = AppwriteService.BUCKET_ID,
            fileId = "unique()",
            file = io.appwrite.models.InputFile.fromFile(file)
        )
        return response.id
    }

    private fun carregarDadosAtuais() {
        if (currentUser == null) return
        lifecycleScope.launch {
            try {
                val doc = AppwriteService.getDatabase().getDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_USUARIOS,
                    documentId = currentUser.uid
                )
                etNome.setText(doc.data["nome"]?.toString())
                // Aqui você usaria o ID da fotoCapa para carregar a imagem com Glide
            } catch (e: Exception) {
                etNome.setText(currentUser.displayName)
            }
        }
    }
}