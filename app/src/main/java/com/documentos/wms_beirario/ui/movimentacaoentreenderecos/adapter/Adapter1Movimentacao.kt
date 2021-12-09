package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMovimentacao1Binding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementResponseModel1

class Adapter1Movimentacao(private val onclick: (MovementResponseModel1) -> Unit) :
    ListAdapter<MovementResponseModel1, Adapter1Movimentacao.Adapter1MovimentacaoViewHolder>(
        DiffUltilCallBack()
    ) {

    inner class Adapter1MovimentacaoViewHolder(private val mBinding: ItemRvMovimentacao1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(list: MovementResponseModel1?) {
            with(mBinding) {
                if (list != null) {
                    documentoApi.text = list.documento.toString()
                    dataApi.text = AppExtensions.formatDataEHora(list.data)
                    operadorApi.text = list.operadorColetor
                    armApi.text = list.idArmazem.toString()
                }
            }
            itemView.setOnClickListener {
                if (list != null) {
                    onclick.invoke(list)
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

private class DiffUltilCallBack : DiffUtil.ItemCallback<MovementResponseModel1>() {
    override fun areItemsTheSame(
        oldItem: MovementResponseModel1,
        newItem: MovementResponseModel1
    ): Boolean {
        return oldItem.idTarefa == newItem.idTarefa
    }

    override fun areContentsTheSame(
        oldItem: MovementResponseModel1,
        newItem: MovementResponseModel1
    ): Boolean {
        return oldItem == newItem
    }

}