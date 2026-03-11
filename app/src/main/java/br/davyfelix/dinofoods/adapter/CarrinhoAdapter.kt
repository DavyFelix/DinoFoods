package br.davyfelix.dinofoods.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.data.Produto

class CarrinhoAdapter(
    private val itens: MutableList<Produto>,
    private val onRemoverClick: (Produto) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    class CarrinhoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNomeProduto)
        val preco: TextView = view.findViewById(R.id.tvPreco)
        val imagem: ImageView = view.findViewById(R.id.imgProduto)
        val btnRemover: ImageButton = view.findViewById(R.id.btnRemover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrinho, parent, false)
        return CarrinhoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val produto = itens[position]
        holder.nome.text = produto.productName
        holder.preco.text = "R$ %.2f".format(produto.price)

        Glide.with(holder.itemView.context).load(produto.imagemID).into(holder.imagem)

        holder.btnRemover.setOnClickListener {
            onRemoverClick(produto)
        }
    }

    override fun getItemCount() = itens.size
}