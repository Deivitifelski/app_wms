package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMovimentacao2Binding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementReturnItemClickMov

class Adapter2Movimentacao() :
    ListAdapter<MovementReturnItemClickMov, Adapter2Movimentacao.Adapter2MovimentacaoViewHolder>(
        DiffUltilCallBack2()
    ) {

    inner class Adapter2MovimentacaoViewHolder(private val mBinding: ItemRvMovimentacao2Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(list: MovementReturnItemClickMov?) {
            with(mBinding) {
                if (list != null) {
                    txtApiNumeroserie.text = list.numeroSerie
                    txtApiEndereO.text = list.enderecoVisual
                    txtApiData.text = AppExtensions.formatDataEHora(list.dataInclusao)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Adapter2MovimentacaoViewHolder {
        val binding =
            ItemRvMovimentacao2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Adapter2MovimentacaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Adapter2MovimentacaoViewHolder, position: Int) {
        val list = getItem(position)
        holder.bind(list)
    }

}

private class DiffUltilCallBack2 : DiffUtil.ItemCallback<MovementReturnItemClickMov>() {
    override fun areItemsTheSame(
        oldItemMovement: MovementReturnItemClickMov,
        newItemMovement: MovementReturnItemClickMov
    ): Boolean {
        return oldItemMovement.sequencial == newItemMovement.sequencial
    }

    override fun areContentsTheSame(
        oldItemMovement: MovementReturnItemClickMov,
        newItemMovement: MovementReturnItemClickMov
    ): Boolean {
        return newItemMovement == oldItemMovement
    }


}