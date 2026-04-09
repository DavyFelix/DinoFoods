package br.davyfelix.dinofoods.fragments

import Orders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.davyfelix.dinofoods.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsFragment : Fragment() {

    private var pedido: Orders? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Método seguro que funciona em versões novas (Android 13+) e antigas
        pedido = if (android.os.Build.VERSION.SDK_INT >= 33) {
            arguments?.getSerializable("pedido", Orders::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("pedido") as? Orders
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_details, container, false)

        val tvId = view.findViewById<TextView>(R.id.tvIdDetalhePedido)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatusDetalhePedido)
        val tvData = view.findViewById<TextView>(R.id.tvDataDetalhePedido)
        val tvItens = view.findViewById<TextView>(R.id.tvItensDetalhePedido)
        val btnVoltar = view.findViewById<ImageButton>(R.id.btnVoltarOrderDetails)

        pedido?.let { p ->
            tvId.text = "Pedido #${p.id.takeLast(6).uppercase()}"
            tvStatus.text = "Status: ${p.status.replaceFirstChar { it.uppercase() }}"

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvData.text = "Data: ${sdf.format(Date(p.timestamp))}"

            // --- TRATAMENTO DO JSON ---
            tvItens.text = formatarListaItens(p.itens)
        }

        btnVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    /**
     * Converte o JSON de itens em uma String organizada linha por linha
     */
    private fun formatarListaItens(jsonItens: String): String {
        return try {
            val gson = Gson()
            // Define o tipo da lista (Lista de Maps com Chave String e Valor Qualquer)
            val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val lista: List<Map<String, Any>> = gson.fromJson(jsonItens, itemType)

            val builder = StringBuilder()
            var totalPedido = 0.0

            lista.forEach { item ->
                val nome = item["productName"] ?: "Item desconhecido"
                val preco = (item["price"] as? Number)?.toDouble() ?: 0.0

                // Formata cada linha: "• Nome do Item - R$ 00,00"
                builder.append("• $nome - R$ %.2f\n".format(preco))
                totalPedido += preco
            }

            builder.append("\nTotal do Pedido: R$ %.2f".format(totalPedido))
            builder.toString()

        } catch (e: Exception) {
            // Se o JSON for o formato antigo (gigante) ou der erro, mostra o texto original
            "Erro ao processar itens ou formato antigo:\n$jsonItens"
        }
    }
}