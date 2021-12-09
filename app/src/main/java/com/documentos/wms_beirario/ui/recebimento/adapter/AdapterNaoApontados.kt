package com.documentos.wms_beirario.ui.recebimento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvApontadosBinding
import com.documentos.wms_beirario.model.recebimento.NumerosSerieNaoApontado

class AdapterNaoApontados :
    ListAdapter<NumerosSerieNaoApontado, AdapterNaoApontados.AdapterNaoApontadosViewHolder>(
        DiffUtilNaoApontados()
    ) {

    inner class AdapterNaoApontadosViewHolder(val mBinding: ItemRvApontadosBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: NumerosSerieNaoApontado?) {
            mBinding.itNumSerieRecebimentoApontados.text = item!!.numeroSerie
            mBinding.itSequencialRecebimentoApontados.text = item.sequencial.toString()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterNaoApontadosViewHolder {
        val binding =
            ItemRvApontadosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterNaoApontadosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterNaoApontadosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilNaoApontados : DiffUtil.ItemCallback<NumerosSerieNaoApontado>() {
    override fun areItemsTheSame(
        oldItem: NumerosSerieNaoApontado,
        newItem: NumerosSerieNaoApontado
    ): Boolean {
        return oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(
        oldItem: NumerosSerieNaoApontado,
        newItem: NumerosSerieNaoApontado
    ): Boolean {
        return oldItem == newItem
    }

}