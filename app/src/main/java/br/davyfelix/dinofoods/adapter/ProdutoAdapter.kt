package br.davyfelix.dinofoods.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // Importante
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Importante
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.model.Produto

class ProdutoAdapter(
    private val lista: List<Produto>
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.tvNomeProduto)
        val descricao: TextView = itemView.findViewById(R.id.tvDescricao)
        val preco: TextView = itemView.findViewById(R.id.tvPreco)
        val btnAdicionar: Button = itemView.findViewById(R.id.btnAdicionar)
        // 1. Referência para o ImageView (certifique-se de que o ID no XML é este)
        val imagem: ImageView = itemView.findViewById(R.id.imgProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = lista[position]

        holder.nome.text = produto.productName
        holder.descricao.text = produto.description
        holder.preco.text = "R$ %.2f".format(produto.price)

        // 2. Carregando a imagem com Glide
        Glide.with(holder.itemView.context)
            .load(produto.imagemUrl) // A URL que geramos no AppwriteService
            //.placeholder(R.drawable.loading_placeholder) // Uma imagem cinza enquanto carrega
            //.error(R.drawable.error_image) // Imagem caso o link falhe
            .centerCrop()
            .into(holder.imagem)

        holder.btnAdicionar.setOnClickListener {
            Carrinho.adicionar(produto)
            Toast.makeText(
                holder.itemView.context,
                "${produto.productName} adicionado!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = lista.size
}