package com.documentos.wms_beirario.ui.productionreceipt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemReceiptProduct1Binding
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1

class AdapterReceiptProduct1(private val onClick:(ReceiptProduct1) ->Unit):ListAdapter<ReceiptProduct1, AdapterReceiptProduct1.AdapterReceiptProductVH1>(
    DiffUtilReceipt1()
) {
    inner class AdapterReceiptProductVH1(val binding : ItemReceiptProduct1Binding):RecyclerView.ViewHolder(binding.root){
        fun bind(listReceipt: ReceiptProduct1?) {
            with(binding){
                listReceipt.let {
                    itPedido.text = listReceipt!!.pedido
                    itAreaArm.text = listReceipt.areaDestino
                    itProgramado.text = listReceipt.pedidoProgramado
                    itQuant.text = listReceipt.quantidadeVolumes.toString()
                    itemView.setOnClickListener {
                        onClick.invoke(listReceipt)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterReceiptProductVH1 {
    val binding = ItemReceiptProduct1Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterReceiptProductVH1(binding)
    }

    override fun onBindViewHolder(holder: AdapterReceiptProductVH1, position: Int) {
       holder.bind(getItem(position))
    }
}

class DiffUtilReceipt1():DiffUtil.ItemCallback<ReceiptProduct1>() {
    override fun areItemsTheSame(oldItem: ReceiptProduct1, newItem: ReceiptProduct1): Boolean {
        return oldItem.pedido == newItem.pedido
    }

    override fun areContentsTheSame(oldItem: ReceiptProduct1, newItem: ReceiptProduct1): Boolean {
        return oldItem == newItem
    }

}
