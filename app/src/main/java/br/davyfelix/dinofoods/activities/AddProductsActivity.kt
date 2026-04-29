package br.davyfelix.dinofoods.activities

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.databinding.ActivityAddProductsBinding // Recomendo usar ViewBinding
import br.davyfelix.dinofoods.services.AppwriteService
import io.appwrite.ID
import io.appwrite.models.InputFile
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddProductsActivity : AppCompatActivity() {

    // Usando ViewBinding para facilitar o acesso aos IDs do XML
    private lateinit var binding: ActivityAddProductsBinding
    private var imageUri: Uri? = null

    // Seletor de Galeria
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivProductImage.setImageURI(uri)
            binding.ivProductImage.setPadding(0, 0, 0, 0) // Remove o padding do ícone
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        AppwriteService.init(applicationContext)

        // Configuração de Padding para Notch/Sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Clique na imagem para selecionar da galeria
        binding.cardImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Clique no botão de cadastrar
        binding.btnAddProduct.setOnClickListener {
            validateAndSave()
        }
    }

    private fun validateAndSave() {
        val name = binding.etProductName.text.toString()
        val price = binding.etPrice.text.toString().toDoubleOrNull()
        val stock = binding.etStock.text.toString().toIntOrNull()
        val category = binding.etCategory.text.toString()

        if (name.isEmpty() || price == null || stock == null || imageUri == null) {
            Toast.makeText(this, "Preencha tudo e selecione uma foto!", Toast.LENGTH_SHORT).show()
            return
        }

        saveProduct(name, price, stock, category)
    }

    private fun saveProduct(name: String, price: Double, stock: Int, category: String) {
        lifecycleScope.launch {
            try {
                // 1. Fazer Upload da Imagem para o Bucket do Appwrite
                val imageFileId = uploadImage()

                // 2. Criar Documento no Banco de Dados
                val data = mapOf(
                    "productName" to name,
                    "price" to price,
                    "stockQuantity" to stock,
                    "category" to category,
                    "imageId" to imageFileId // Guardamos o ID da imagem para buscar depois
                )

                AppwriteService.getDatabase().createDocument(
                    databaseId = AppwriteService.DATABASE_ID,
                    collectionId = AppwriteService.COLLECTION_PRODUTOS,
                    documentId = ID.unique(),
                    data = data
                )

                Toast.makeText(this@AddProductsActivity, "Sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Volta para a tela anterior

            } catch (e: Exception) {
                Toast.makeText(this@AddProductsActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun uploadImage(): String {
        val inputStream = contentResolver.openInputStream(imageUri!!)
        val file = File(cacheDir, "temp_image_product.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        // Substitua BUCKET_ID pelo ID do seu storage no Appwrite
        val response = AppwriteService.getStorage().createFile(
            bucketId = "69a7874e00169355f884",
            fileId = ID.unique(),
            file = InputFile.fromFile(file)
        )
        return response.id
    }
}