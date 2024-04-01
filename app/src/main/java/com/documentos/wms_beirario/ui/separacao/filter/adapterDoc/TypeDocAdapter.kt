package com.documentos.wms_beirario.ui.separacao.filter.adapterDoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvFilterSeparationBinding

class TypeDocAdapter(val onClick: (List<String>) -> Unit) :
    RecyclerView.Adapter<TypeDocAdapter.TypeDocAdapterVh>() {

    var list = mutableListOf<String>()
    var selectedItems = mutableListOf<String>()

    inner class TypeDocAdapterVh(val binding: ItemRvFilterSeparationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding) {
                itAndarSeparacao1.text = item
                checkboxSeparacao1Andar.isChecked = selectedItems.contains(item)
                itemView.setOnClickListener {
                    select(item, binding)
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
            onClick.invoke(selectedItems)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeDocAdapterVh {
        val i =
            ItemRvFilterSeparationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TypeDocAdapterVh(i)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TypeDocAdapterVh, position: Int) {
        holder.bind(list[position])
    }

    fun updateDoc(newList: List<String>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedItems.clear()
        selectedItems.addAll(list)
        onClick.invoke(selectedItems)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedItems.clear()
        onClick.invoke(selectedItems)
        notifyDataSetChanged()
    }

    fun getSelectedItemsList(): List<String> {
        return selectedItems.toList()
    }

}
