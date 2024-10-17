package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.RvItemNfRfidBinding


class ListagemNfAdapterRfid :
    RecyclerView.Adapter<ListagemNfAdapterRfid.ListagemNfAdapterRfidVh>() {


    inner class ListagemNfAdapterRfidVh(val binding: RvItemNfRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
//            if (item.conferida) {
//                binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
//            } else {
//                binding.layoutParent.setBackgroundResource(R.color.white)
//            }

//            binding.textNf.text = item.nf
//            binding.textSerie.text = item.remessa
//            binding.textQtdEtiquetas.text = "Quantidade de etiquetas: ${item.qtdEtiquetas}"
//            binding.textFilial.text = item.filial
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListagemNfAdapterRfidVh {
        val binding =
            RvItemNfRfidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListagemNfAdapterRfidVh(binding)
    }

    override fun getItemCount() = 0

    override fun onBindViewHolder(holder: ListagemNfAdapterRfidVh, position: Int) {
    }

//    fun updateData(listNf: List<RecebimentoRfid>) {
//        list.clear()
//        list.addAll(listNf)
//        notifyDataSetChanged()
//    }

}