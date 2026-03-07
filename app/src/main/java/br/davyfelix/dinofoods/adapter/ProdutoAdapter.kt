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
import br.davyfelix.dinofoods.model.Produto

class ProdutoAdapter(
    private val lista: List<Produto>,
    // Adicionamos um callback opcional para o Fragment saber quando algo foi adicionado
    private val onProdutoAdicionado: ((Produto) -> Unit)? = null
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNomeProduto)
        //val descricao: TextView = view.findViewById(R.id.tvDescricao)
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
       // holder.descricao.text = produto.description
        holder.preco.text = "R$ %.2f".format(produto.price)

        // Carregamento da imagem com Glide
        Glide.with(holder.itemView.context)
            .load(produto.imagemUrl)
            .centerCrop()
            .into(holder.imagem)

        holder.btnAdicionar.setOnClickListener {
            // 1. Lógica de dados
            Carrinho.adicionar(produto)

            // 2. Feedback visual imediato
            Toast.makeText(
                holder.itemView.context,
                "${produto.productName} adicionado ao carrinho!",
                Toast.LENGTH_SHORT
            ).show()

            // 3. Notifica o Fragment (útil para atualizar contadores no FAB)
            onProdutoAdicionado?.invoke(produto)
        }
    }

    override fun getItemCount(): Int = lista.size
}