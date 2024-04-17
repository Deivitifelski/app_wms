package com.documentos.wms_beirario.ui.separacao.filter.adapterTransportadora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvFilterSeparationBinding
import com.documentos.wms_beirario.model.separation.filtros.ResponseDocTransSeparacao

class TypeTransportadoraAdapter(
    val onClick: (Pair<List<String>, Boolean>) -> Unit
) :
    RecyclerView.Adapter<TypeTransportadoraAdapter.TypeTransportadoraAdapterVh>() {

    var list = mutableListOf<ResponseDocTransSeparacao>()
    var selectedItems = mutableListOf<String>()

    inner class TypeTransportadoraAdapterVh(val binding: ItemRvFilterSeparationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseDocTransSeparacao) {
            with(binding) {
                itAndarSeparacao1.text = item.descricao
                checkboxSeparacao1Andar.isChecked = selectedItems.contains(item.id.toString())
                itemView.setOnClickListener {
                    select(item.id.toString(), binding)
                }
                checkboxSeparacao1Andar.setOnClickListener {
                    select(item.id.toString(), binding)
                }
            }
        }

        private fun select(item: String, layout: ItemRvFilterSeparationBinding) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                layout.checkboxSeparacao1Andar.isChecked = false
            } else {
                selectedItems.add(item)
                layout.checkboxSeparacao1Andar.isChecked = true
            }
            if (selectedItems.size == list.size) {
                onClick.invoke(Pair(first = selectedItems, second = true))
            } else {
                onClick.invoke(Pair(first = selectedItems, second = false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeTransportadoraAdapterVh {
        val i =
            ItemRvFilterSeparationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TypeTransportadoraAdapterVh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TypeTransportadoraAdapterVh, position: Int) {
        holder.bind(list[position])
    }

    fun updateDoc(newList: List<ResponseDocTransSeparacao>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedItems.clear()
        list.forEach {
            selectedItems.add(it.id.toString())
        }
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun clearSelectionSaidaNf() {
        selectedItems.clear()
        list.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemsList(): List<String> {
        return selectedItems.toList()
    }

}
