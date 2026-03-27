package br.davyfelix.dinofoods.services

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppwriteService {
    private lateinit var client: Client
    private lateinit var databases: Databases
    private lateinit var storage: Storage

    // IDs do seu console Appwrite
    const val DATABASE_ID = "69a784c50036d1da880b"
    const val COLLECTION_PRODUTOS = "produtos"

    const val COLLECTION_PEDIDOS = "pedido"

    // No seu AppwriteService.kt

    fun init(context: Context) {
        client = Client(context)
            // Alterado de "cloud" para "nyc.cloud" conforme sua imagem
            .setEndpoint("https://nyc.cloud.appwrite.io/v1")
            .setProject("69a7800f0010f71f3348")

        databases = Databases(client)
        storage = Storage(client)
    }
    fun getDatabase(): Databases {
        return databases
    }

    fun getImageUrl(fileId: String): String {
        return "https://nyc.cloud.appwrite.io/v1/storage/buckets/69a7874e00169355f884/files/$fileId/view?project=69a7800f0010f71f3348&mode=admin"
    }

    // Função para buscar produtos
    suspend fun getProdutos() = databases.listDocuments(
        databaseId = DATABASE_ID,
        collectionId = COLLECTION_PRODUTOS
    )
    suspend fun getPedidos() = databases.listDocuments(
        databaseId = DATABASE_ID,
        collectionId = COLLECTION_PEDIDOS
    )
}