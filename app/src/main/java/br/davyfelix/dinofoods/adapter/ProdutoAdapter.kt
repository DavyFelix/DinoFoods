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
    // Adicionamos este parâmetro para controlar se mostra a lixeira ou o botão de adicionar
    private val layoutDelecao: Boolean = false,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNomeProduto)
        val preco: TextView = view.findViewById(R.id.tvPreco)
        val btnAdicionar: Button = view.findViewById(R.id.btnAdicionar)
        val imagem: ImageView = view.findViewById(R.id.imgProduto)
        // Adicione este ID no seu XML item_produto.xml
        val btnLixeira: ImageView = view.findViewById(R.id.btnLixeira)
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

        // --- LÓGICA DE ALTERNÂNCIA DE LAYOUT ---
        if (layoutDelecao) {
            // MODO ADMIN: Esconde botão adicionar e mostra lixeira
            holder.btnAdicionar.visibility = View.GONE
            holder.btnLixeira.visibility = View.VISIBLE

            // No modo admin, o clique no item inteiro não faz nada (opcional)
            holder.itemView.setOnClickListener(null)

            // O clique para excluir acontece na lixeira
            holder.btnLixeira.setOnClickListener {
                onItemClick(produto)
            }
        } else {
            // MODO LOJA: Mostra botão adicionar e esconde lixeira
            holder.btnAdicionar.visibility = View.VISIBLE
            holder.btnLixeira.visibility = View.GONE

            // Clique no card abre detalhes
            holder.itemView.setOnClickListener {
                onItemClick(produto)
            }

            // Clique no botão adiciona ao carrinho
            holder.btnAdicionar.setOnClickListener {
                val context = holder.itemView.context
                Carrinho.adicionar(produto)
                Toast.makeText(
                    context,
                    context.getString(R.string.adicionadoItem, produto.productName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun updateList(novaLista: List<Produto>) {
        this.lista = novaLista
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = lista.size
}