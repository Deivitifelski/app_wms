package com.documentos.wms_beirario.ui.unmountingVolumes.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvUnmountingVolumes1Binding
import com.documentos.wms_beirario.model.desmontagemVol.UnmountingVolumes1Item


class DisassamblyAdapter(val onClick: (UnmountingVolumes1Item) -> Unit) :
    RecyclerView.Adapter<DisassamblyAdapter.DisassamblyAdapterViewHolder>() {

    private var mListaTaskDisassambly1: MutableList<UnmountingVolumes1Item> = mutableListOf()

    inner class DisassamblyAdapterViewHolder(val bindin: ItemRvUnmountingVolumes1Binding) :
        RecyclerView.ViewHolder(bindin.root) {

        fun geraItem(unmountingVolumes1Item: UnmountingVolumes1Item) {
            with(bindin) {
                enderecoVisualApi.text = unmountingVolumes1Item.enderecoVisual
                qntApi.text = unmountingVolumes1Item.quantidadeVolumes.toString()
                itemView.setOnClickListener {
                    onClick.invoke(unmountingVolumes1Item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisassamblyAdapterViewHolder {
        val binding =
            ItemRvUnmountingVolumes1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DisassamblyAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DisassamblyAdapterViewHolder, position: Int) {
        holder.geraItem(mListaTaskDisassambly1[position])
    }

    override fun getItemCount() = mListaTaskDisassambly1.size
    fun update(listPendent: List<UnmountingVolumes1Item>) {
        mListaTaskDisassambly1.clear()
        mListaTaskDisassambly1.addAll(listPendent)
        notifyDataSetChanged()
    }

}