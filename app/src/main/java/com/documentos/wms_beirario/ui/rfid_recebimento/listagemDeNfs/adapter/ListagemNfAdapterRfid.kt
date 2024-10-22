package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNfs.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.RvItemNfRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.somSucess


class ListagemNfAdapterRfid(
    private val onNfClick: (List<ResponseGetRecebimentoNfsPendentes>) -> Unit
) : RecyclerView.Adapter<ListagemNfAdapterRfid.ListagemNfAdapterRfidVh>() {

    private var list = mutableListOf<ResponseGetRecebimentoNfsPendentes>()
    private var selectedItems: MutableList<ResponseGetRecebimentoNfsPendentes> = mutableListOf()

    inner class ListagemNfAdapterRfidVh(val binding: RvItemNfRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ResponseGetRecebimentoNfsPendentes, isSelected: Boolean) {
            binding.textNf.text = "Nf: ${item.nfNumero}"
            binding.textSerie.text = "Série: ${item.nfSerie}"
            binding.textQtdEtiquetas.text = "Qtd.Etiquetas: ${item.quantidadeNumeroSerie}"
            binding.textFilial.text = "Filial: ${item.filial}"

            if (isSelected) binding.check.visibility = ViewGroup.VISIBLE else binding.check.visibility = ViewGroup.INVISIBLE


            // Clique no item
            binding.root.setOnClickListener {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                } else {
                    selectedItems.add(item)
                }
                notifyItemChanged(adapterPosition)
                onNfClick(selectedItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListagemNfAdapterRfidVh {
        val binding =
            RvItemNfRfidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListagemNfAdapterRfidVh(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ListagemNfAdapterRfidVh, position: Int) {
        val item = list[position]
        val isSelected = selectedItems.contains(item)
        holder.bind(item, isSelected)
    }

    fun updateLista(lista: List<ResponseGetRecebimentoNfsPendentes>) {
        list.clear()
        list.addAll(lista)
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: ListagemNfAdapterRfidVh) {
        super.onViewRecycled(holder)
    }


    fun containsInList(context: Activity, scan: String) {
        try {
            list.find { it.nfNumero.toString() == scan || it.idDocumento == scan }?.let {
                context.somSucess()
                if (!selectedItems.contains(it)) {
                    selectedItems.add(it)
                    onNfClick(selectedItems)
                    notifyDataSetChanged()
                } else {
                    selectedItems.remove(it)
                    onNfClick(selectedItems)
                    notifyDataSetChanged()
                }
            }
                ?: context.alertDefaulSimplesError(message = "Nota fiscal não encontrada na listagem:\n$scan")
        } catch (e: Exception) {
            context.alertDefaulSimplesError(message = "Ocorreu um erro ao tentar selecionar a nota fiscal\n${e.message}")
        }
    }
}
