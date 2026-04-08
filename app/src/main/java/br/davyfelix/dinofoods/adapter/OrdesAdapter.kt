import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.davyfelix.dinofoods.R
import java.text.SimpleDateFormat
import java.util.*

class OrdesAdapter(private val pedidos: List<Ordes>) :
    RecyclerView.Adapter<OrdesAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvIdPedido)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvData: TextView = view.findViewById(R.id.tvData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ordes, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.tvId.text = "Pedido: ${pedido.id.takeLast(6).uppercase()}"
        holder.tvStatus.text = "Status: ${pedido.status}"

        // Formatação da data
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dataFormatada = sdf.format(Date(pedido.timestamp))
        holder.tvData.text = dataFormatada
    }

    override fun getItemCount() = pedidos.size
}