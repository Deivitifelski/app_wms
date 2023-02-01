package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMovimentacao1Binding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementResponseModel1
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.ResponseTaskOPeration1
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.ResponseTaskOPerationItem1
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class Adapter1Movimentacao() :
    ListAdapter<ResponseTaskOPerationItem1, Adapter1Movimentacao.Adapter1MovimentacaoViewHolder>(
        DiffUltilCallBack()
    ) {

    inner class Adapter1MovimentacaoViewHolder(private val mBinding: ItemRvMovimentacao1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(list: ResponseTaskOPerationItem1?) {
            with(mBinding) {
                if (list != null) {
                    endOrigemApi.text = list.enderecovisual
                    dataApi.text = AppExtensions.formatDataEHoraMov(list.datainclusao)
                    skuApi.text = list.sku
                    quantidadeApi.text = list.quantidade.toString()
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

class DiffUltilCallBack : DiffUtil.ItemCallback<ResponseTaskOPerationItem1>() {
    override fun areItemsTheSame(
        oldItem: ResponseTaskOPerationItem1,
        newItem: ResponseTaskOPerationItem1
    ): Boolean {
        return oldItem.sku == newItem.sku
                && oldItem.numeroserie == newItem.numeroserie
    }

    override fun areContentsTheSame(
        oldItem: ResponseTaskOPerationItem1,
        newItem: ResponseTaskOPerationItem1
    ): Boolean {
        return oldItem == newItem
    }

}