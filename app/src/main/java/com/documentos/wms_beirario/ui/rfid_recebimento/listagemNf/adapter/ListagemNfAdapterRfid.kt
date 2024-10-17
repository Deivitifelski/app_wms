package com.documentos.wms_beirario.ui.rfid_recebimento.listagemNf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.RvItemNfRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfid


class ListagemNfAdapterRfid :
    RecyclerView.Adapter<ListagemNfAdapterRfid.ListagemNfAdapterRfidVh>() {

    private var list = mutableListOf<RecebimentoRfid>()

    inner class ListagemNfAdapterRfidVh(val binding: RvItemNfRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecebimentoRfid) {
            if (item.conferida) {
                binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
            } else {
                binding.layoutParent.setBackgroundResource(R.color.white)
            }

            binding.textNf.text = item.nf
            binding.textSerie.text = item.remessa
            binding.textQtdEtiquetas.text = "Quantidade de etiquetas: ${item.qtdEtiquetas}"
            binding.textFilial.text = item.filial
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListagemNfAdapterRfidVh {
        val binding =
            RvItemNfRfidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListagemNfAdapterRfidVh(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListagemNfAdapterRfidVh, position: Int) {
        holder.bind(list[position])
    }

    fun updateData(listNf: List<RecebimentoRfid>) {
        list.clear()
        list.addAll(listNf)
        notifyDataSetChanged()
    }

}