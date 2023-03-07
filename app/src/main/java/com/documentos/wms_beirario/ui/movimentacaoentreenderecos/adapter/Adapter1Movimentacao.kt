package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMovimentacao1Binding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.ListItens
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class Adapter1Movimentacao() :
    ListAdapter<ListItens, Adapter1Movimentacao.Adapter1MovimentacaoViewHolder>(
        DiffUltilCallBack()
    ) {

    inner class Adapter1MovimentacaoViewHolder(private val mBinding: ItemRvMovimentacao1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(list: ListItens?) {
            with(mBinding) {
                if (list != null) {
                    endOrigemApi.text = list.enderecoVisual
                    dataApi.text = AppExtensions.formatDataEHora(list.dataHoraInclusao)
                    skuApi.text = list.sku
                    quantidadeApi.text = list.quantidade.toString()
                    if (!list.numeroSerie.isNullOrEmpty()) {
                        txtNumSerie.text = "N°Série"
                        numSerieOrEanApi.text = list.numeroSerie?.ifEmpty { " - " }
                    } else {
                        txtNumSerie.text = "Ean"
                        numSerieOrEanApi.text = list.ean.ifEmpty { " - " }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Adapter1MovimentacaoViewHolder {
        val binding =
            ItemRvMovimentacao1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Adapter1MovimentacaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Adapter1MovimentacaoViewHolder, position: Int) {
        val list = getItem(position)
        holder.bind(list)
    }

}

class DiffUltilCallBack : DiffUtil.ItemCallback<ListItens>() {
    override fun areItemsTheSame(
        oldItem: ListItens,
        newItem: ListItens
    ): Boolean {
        return oldItem.sku == newItem.sku
                && oldItem.numeroSerie == newItem.numeroSerie
    }

    override fun areContentsTheSame(
        oldItem: ListItens,
        newItem: ListItens
    ): Boolean {
        return oldItem == newItem
    }

}