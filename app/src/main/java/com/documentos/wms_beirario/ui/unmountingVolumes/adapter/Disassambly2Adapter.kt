package com.documentos.wms_beirario.ui.unmountingVolumes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvUnmountingVolumes1Binding
import com.documentos.wms_beirario.model.desmontagemVol.ResponseUnmonting2Item


class Disassambly2Adapter(val onClick: (ResponseUnmonting2Item) -> Unit) :
    RecyclerView.Adapter<Disassambly2Adapter.Disassambly2AdapterViewHolder>() {

    private var mListaTaskDisassambly1: MutableList<ResponseUnmonting2Item> = mutableListOf()

    inner class Disassambly2AdapterViewHolder(val bindin: ItemRvUnmountingVolumes1Binding) :
        RecyclerView.ViewHolder(bindin.root) {

        fun geraItem(unmountingVolumes1Item: ResponseUnmonting2Item) {
            with(bindin) {
                enderecoVisualApi.text = unmountingVolumes1Item.nome
                siglaAreaApi.text = unmountingVolumes1Item.quantidadeVolumes.toString()
            }
            itemView.setOnClickListener {
                onClick.invoke(unmountingVolumes1Item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Disassambly2AdapterViewHolder {
        val binding =
            ItemRvUnmountingVolumes1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Disassambly2AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Disassambly2AdapterViewHolder, position: Int) {
        holder.geraItem(mListaTaskDisassambly1[position])
    }

    override fun getItemCount() = mListaTaskDisassambly1.size
    fun update(listPendent: List<ResponseUnmonting2Item>) {
        mListaTaskDisassambly1.clear()
        mListaTaskDisassambly1.addAll(listPendent)
        notifyDataSetChanged()
    }

}