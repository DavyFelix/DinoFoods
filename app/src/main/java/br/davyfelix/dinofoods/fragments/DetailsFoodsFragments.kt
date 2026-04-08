package br.davyfelix.dinofoods.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.davyfelix.dinofoods.R
import br.davyfelix.dinofoods.data.Carrinho
import br.davyfelix.dinofoods.data.Produto
import com.bumptech.glide.Glide // Ou a lib que você usa para imagem

class DetailsFoodsFragment : Fragment() {

    private var produto: Produto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recupera o produto enviado pelo bundle
        produto = arguments?.getSerializable("produto") as? Produto
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details_products, container, false)

        val img = view.findViewById<ImageView>(R.id.imgDetalhe)
        val nome = view.findViewById<TextView>(R.id.tvNomeDetalhe)
        val preco = view.findViewById<TextView>(R.id.tvPrecoDetalhe)
        val desc = view.findViewById<TextView>(R.id.tvDescricaoDetalhe)
        val btnAdd = view.findViewById<Button>(R.id.btnAdicionarDetalhe)
        val btnVoltar = view.findViewById<ImageView>(R.id.btnBackF)

        produto?.let { p ->
            nome.text = p.productName
            preco.text = "R$ %.2f".format(p.price)
            desc.text = p.description

            if (!p.imagemID.isNullOrEmpty()) {
                Glide.with(this).load(p.imagemID).into(img)
            }

            btnAdd.setOnClickListener {
                Carrinho.itens.add(p)
                Toast.makeText(requireContext(), "${p.productName} adicionado!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Volta para a lista após adicionar
            }
        }

        btnVoltar.setOnClickListener { parentFragmentManager.popBackStack() }

        return view
    }
}