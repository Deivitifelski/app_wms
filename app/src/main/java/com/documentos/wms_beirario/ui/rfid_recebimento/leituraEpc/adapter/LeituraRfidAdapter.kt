package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.LeituraRfidEpc
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfid


class LeituraRfidAdapter :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {

    private var list = mutableListOf<LeituraRfidEpc>()

    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeituraRfidEpc) {
            if (item.conferida) {
                binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
            } else {
                binding.layoutParent.setBackgroundResource(R.color.white)
            }

            binding.textIdentificacao.text = item.tag
            binding.textInfo.text = item.descricao
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeituraRfidEpcAdapterRfidVh {
        val binding =
            ItemRvEpcRfidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeituraRfidEpcAdapterRfidVh(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: LeituraRfidEpcAdapterRfidVh, position: Int) {
        holder.bind(list[position])
    }

    fun updateData(listNf: List<LeituraRfidEpc>) {
        list.clear()
        list.addAll(listNf)
        notifyDataSetChanged()
    }

}