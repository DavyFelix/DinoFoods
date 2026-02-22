package br.davyfelix.dinofoods.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)

        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {

        val produto = lista[position] // ✅ primeiro cria

        holder.nome.text = produto.nome
        holder.descricao.text = produto.descricao
        holder.preco.text = "R$ %.2f".format(produto.preco)

        holder.btnAdicionar.setOnClickListener {
            Carrinho.adicionar(produto)

            Toast.makeText(
                holder.itemView.context,
                "${produto.nome} adicionado ao carrinho!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = lista.size
}