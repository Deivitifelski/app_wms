package com.documentos.wms_beirario.ui.separacao.filter.adapterDoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvFilterSeparationBinding
import com.documentos.wms_beirario.model.separation.filtros.ResponseDocTransSeparacao

class TypeDocAdapter(val onClick: (List<String>) -> Unit) :
    RecyclerView.Adapter<TypeDocAdapter.TypeDocAdapterVh>() {

    var list = mutableListOf<ResponseDocTransSeparacao>()
    var selectedItems = mutableListOf<String>()

    inner class TypeDocAdapterVh(val binding: ItemRvFilterSeparationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseDocTransSeparacao) {
            with(binding) {
                itAndarSeparacao1.text = item.descricao
                checkboxSeparacao1Andar.isChecked = selectedItems.contains(item.id.toString())
                itemView.setOnClickListener {
                    select(item.id.toString(), binding)
                }
            }
        }

        private fun select(item: String, layout: ItemRvFilterSeparationBinding) {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
                layout.checkboxSeparacao1Andar.isChecked = false
                onClick.invoke(selectedItems)
            } else {
                selectedItems.add(item)
                layout.checkboxSeparacao1Andar.isChecked = true
                onClick.invoke(selectedItems)
            }

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
//        onClick.invoke(selectedItems)
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedItems.clear()
//        onClick.invoke(selectedItems)
        notifyDataSetChanged()
    }

    fun getSelectedItemsList(): List<String> {
        return selectedItems.toList()
    }

}
