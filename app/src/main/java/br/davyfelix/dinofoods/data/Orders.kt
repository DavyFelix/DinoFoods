import java.io.Serializable

data class Orders(
    val id: String,
    val status: String,
    val timestamp: Long,
    val itens: String // O JSON dos produtos
) : Serializable