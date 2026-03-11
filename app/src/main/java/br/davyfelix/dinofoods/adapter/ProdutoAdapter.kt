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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.data.Produto


class ProdutoAdapter(
    private val lista: List<Produto>,
    private val onProdutoAdicionado: ((Produto) -> Unit)? = null
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

        // Usando as strings do strings.xml para manter a formatação de moeda
        // Se preferir manter fixo em PT-BR: "R$ %.2f".format(produto.price)
        holder.preco.text = holder.itemView.context.getString(R.string.preco_formatado, produto.price)

        // --- MELHORIA NO CARREGAMENTO DE IMAGEM ---
        Glide.with(holder.itemView.context)
            .load(produto.imagemID) // URL gerada no Fragment/Service
            // Efeito suave ao aparece
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background) // Imagem enquanto carrega
            .error(android.R.drawable.ic_menu_report_image) // Imagem se o ID falhar
            .into(holder.imagem)

        holder.btnAdicionar.setOnClickListener {
            Carrinho.adicionar(produto)

            Toast.makeText(
                holder.itemView.context,
                "${produto.productName} ${holder.itemView.context.getString(R.string.adicionado_sucesso)}",
                Toast.LENGTH_SHORT
            ).show()

            onProdutoAdicionado?.invoke(produto)
        }
    }

    override fun getItemCount(): Int = lista.size
}