package com.example.coletorwms.presenter.consultaCodigoDeBarras

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvProdutoClickBinding
import com.example.coletorwms.model.codBarras.Cod.CodBarrasProdutoClick


class CodBarrasProdutosClickAdapter :
    RecyclerView.Adapter<CodBarrasProdutosClickAdapter.ProdutosViewHolder>() {

    private val mLIstProdutoClick = mutableListOf<CodBarrasProdutoClick>()

    inner class ProdutosViewHolder(val mBinding: ItemRvProdutoClickBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: CodBarrasProdutoClick) {
            with(mBinding) {
                itNomeProdutoClick.text = it.nome
                itSkuProdutoClick.text = it.sku
                itCodEanProdutoClick.text = it.ean
                itTamanhoProdutoClick.text = it.tamanho
                itCodMarcaProdutoClick.text = it.codigoMarca.toString()
                itDesMarcaProdutoClick.text = it.descricaoMarca
                itQuantidadeProdutoClick.text = it.quantidade.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutosViewHolder {
        val mBinding =
            ItemRvProdutoClickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProdutosViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ProdutosViewHolder, position: Int) {
        holder.geraItem(mLIstProdutoClick[position])
    }


    override fun getItemCount() = mLIstProdutoClick.size

    fun update(produtos: List<CodBarrasProdutoClick>) {
        mLIstProdutoClick.clear()
        this.mLIstProdutoClick.addAll(produtos)
        notifyDataSetChanged()
    }


}




