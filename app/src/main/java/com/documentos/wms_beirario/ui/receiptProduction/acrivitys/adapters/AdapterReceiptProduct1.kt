package com.documentos.wms_beirario.ui.receiptProduction.acrivitys.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvRecOrderInitBinding
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct1

class AdapterReceiptProduct1(private val onClick: (ReceiptProduct1) -> Unit) :
    RecyclerView.Adapter<AdapterReceiptProduct1.AdapterReceiptProductVH1>() {

    private val mList = mutableListOf<ReceiptProduct1>()

    inner class AdapterReceiptProductVH1(val binding: ItemRvRecOrderInitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listReceipt: ReceiptProduct1?) {
            with(binding) {
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
        val binding =
            ItemRvRecOrderInitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterReceiptProductVH1(binding)
    }

    override fun onBindViewHolder(holder: AdapterReceiptProductVH1, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size


    fun update(listReceipt: List<ReceiptProduct1>?) {
        mList.clear()
        if (listReceipt != null) {
            mList.addAll(listReceipt)
        }
        notifyDataSetChanged()
    }


}
