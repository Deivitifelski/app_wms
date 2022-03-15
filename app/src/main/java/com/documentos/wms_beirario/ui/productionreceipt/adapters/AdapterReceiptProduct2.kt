package com.documentos.wms_beirario.ui.productionreceipt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemReceiptProduct2Binding
import com.documentos.wms_beirario.model.receiptproduct.ListFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct2

class AdapterReceiptProduct2() :
    ListAdapter<ReceiptProduct2, AdapterReceiptProduct2.AdapterReceiptProductVH2>(
        DiffUtilReceipt2()
    ) {

    inner class AdapterReceiptProductVH2(val binding: ItemReceiptProduct2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listReceipt: ReceiptProduct2?) {
            with(binding) {
                listReceipt.let { list ->
                    itNumSerie.text = list!!.numeroSerie
                    itSku.text = list.sku
                    itSequencial.text = list.sequencial.toString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterReceiptProductVH2 {
        val binding =
            ItemReceiptProduct2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterReceiptProductVH2(binding)
    }

    override fun onBindViewHolder(holder: AdapterReceiptProductVH2, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilReceipt2() : DiffUtil.ItemCallback<ReceiptProduct2>() {
    override fun areItemsTheSame(oldItem: ReceiptProduct2, newItem: ReceiptProduct2): Boolean {
        return oldItem.sku == newItem.sku
    }

    override fun areContentsTheSame(oldItem: ReceiptProduct2, newItem: ReceiptProduct2): Boolean {
        return oldItem == newItem
    }


}
