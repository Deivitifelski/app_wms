package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemReceiptProductOperator1Binding
import com.documentos.wms_beirario.model.receiptproduct.ListReceiptIdOperadorSeriazable
import com.documentos.wms_beirario.model.receiptproduct.ReceiptIdOperador
import com.documentos.wms_beirario.model.receiptproduct.ReceiptIdOperadorSeriazable
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterFilterReceiptProduct(private val onClick: (ReceiptIdOperadorSeriazable) -> Unit) :
    RecyclerView.Adapter<AdapterFilterReceiptProduct.AdapterFilterREceiptProductVH>() {
    private var mList = mutableListOf<ReceiptIdOperadorSeriazable>()


    inner class AdapterFilterREceiptProductVH(private val binding: ItemReceiptProductOperator1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReceiptIdOperadorSeriazable?) {
            binding.itDataFragProd1.text = AppExtensions.formatDataEHora(item!!.minData)
            binding.itUsuarioFragProd1.text = item.usuario
            itemView.setOnClickListener {
                onClick.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterFilterREceiptProductVH {
        val binding = ItemReceiptProductOperator1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterFilterREceiptProductVH(binding)
    }

    override fun onBindViewHolder(holder: AdapterFilterREceiptProductVH, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size
    fun update(list: MutableList<ReceiptIdOperadorSeriazable>) {
        mList.addAll(list)
        notifyDataSetChanged()
    }


}

