package com.documentos.wms_beirario.ui.recebimento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvApontadosBinding
import com.documentos.wms_beirario.model.recebimento.NumerosSerieApontados

class AdapterApontados :
    ListAdapter<NumerosSerieApontados, AdapterApontados.AdapterApontadosViewHolder>(
        DiffUtilApontados()
    ) {

    inner class AdapterApontadosViewHolder(val mBinding: ItemRvApontadosBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: NumerosSerieApontados?) {
            mBinding.itNumSerieRecebimentoApontados.text = item!!.numeroSerie
            mBinding.itSequencialRecebimentoApontados.text = item.sequencial.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterApontadosViewHolder {
        val binding =
            ItemRvApontadosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterApontadosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterApontadosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilApontados : DiffUtil.ItemCallback<NumerosSerieApontados>() {
    override fun areItemsTheSame(
        oldItem: NumerosSerieApontados,
        newItem: NumerosSerieApontados
    ): Boolean {
        return oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(
        oldItem: NumerosSerieApontados,
        newItem: NumerosSerieApontados
    ): Boolean {
        return oldItem == newItem
    }

}
