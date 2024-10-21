package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.RvItemNfRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes


class ListagemNfAdapterRfid(
    private val onNfClick: (ResponseGetRecebimentoNfsPendentes) -> Unit
) : RecyclerView.Adapter<ListagemNfAdapterRfid.ListagemNfAdapterRfidVh>() {

    private var list = mutableListOf<ResponseGetRecebimentoNfsPendentes>()
    private var selectedItems: MutableList<ResponseGetRecebimentoNfsPendentes> = mutableListOf()

    inner class ListagemNfAdapterRfidVh(val binding: RvItemNfRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ResponseGetRecebimentoNfsPendentes, isSelected: Boolean) {
            binding.textNf.text = item.nfNumero.toString()
            binding.textSerie.text = item.nfSerie.toString()
            binding.textQtdEtiquetas.text = "Quantidade de etiquetas: ${item.quantidadeNumeroSerie}"
            binding.textFilial.text = item.filial.toString()

            // Mudar o fundo do item dependendo se ele está selecionado
            binding.root.setBackgroundColor(
                if (isSelected) android.graphics.Color.LTGRAY else android.graphics.Color.WHITE
            )

            // Clique no item
            binding.root.setOnClickListener {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                } else {
                    selectedItems.add(item)
                }
                notifyItemChanged(adapterPosition)
                onNfClick(item)
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

    fun getSelectedItems(): List<ResponseGetRecebimentoNfsPendentes> {
        return selectedItems
    }
}
