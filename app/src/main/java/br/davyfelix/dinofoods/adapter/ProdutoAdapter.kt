package br.davyfelix.dinofoods.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.data.Produto

class ProdutoAdapter(
    private var lista: List<Produto>,
    // Mudamos o nome para ficar claro que é o clique no item para detalhes
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNomeProduto)
        val preco: TextView = view.findViewById(R.id.tvPreco)
        val btnAdicionar: Button = view.findViewById(R.id.btnAdicionar)
        val imagem: ImageView = view.findViewById(R.id.imgProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = lista[position]

        holder.nome.text = produto.productName
        holder.preco.text = holder.itemView.context.getString(R.string.preco_formatado, produto.price)

        Glide.with(holder.itemView.context)
            .load(produto.imagemID)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.imagem)

        // --- COMPORTAMENTO 1: CLIQUE NO CARD (ABRE DETALHES) ---
        holder.itemView.setOnClickListener {
            onItemClick(produto)
        }

        // --- COMPORTAMENTO 2: CLIQUE NO BOTÃO (ADICIONA DIRETO) ---
        holder.btnAdicionar.setOnClickListener {
            val context = holder.itemView.context
            Carrinho.adicionar(produto)
            Toast.makeText(
                holder.itemView.context,
                context.getString(R.string.adicionadoItem, produto.productName),
                Toast.LENGTH_SHORT
            ).show()
            // Se quiser que algo aconteça no Fragment após adicionar, pode manter o invoke aqui
        }
    }

    fun updateList(novaLista: List<Produto>) {
        this.lista = novaLista
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = lista.size
}