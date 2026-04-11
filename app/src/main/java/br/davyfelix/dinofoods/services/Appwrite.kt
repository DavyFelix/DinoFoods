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

    const val COLLECTION_USUARIOS = "usuarios"

    const val COLLECTION_PEDIDOS = "pedido"

    const val BUCKET_ID = "69a7874e00169355f884"

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

    fun getStorage(): Storage = storage

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
    suspend fun salvarPerfilUsuario(uid: String, nome: String, email: String) {
        databases.createDocument(
            databaseId = DATABASE_ID,
            collectionId = COLLECTION_USUARIOS,
            documentId = uid, // Usamos o UID do Firebase como ID do documento
            data = mapOf(
                "nome" to nome,
                "email" to email,
                "fotoCapa" to "" // Vazio por enquanto para a sua rubrica
            )
        )
    }
}