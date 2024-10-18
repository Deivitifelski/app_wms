package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding


class LeituraRfidAdapter(val onclick: (String) -> Unit) :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {


    private var listTags = mutableListOf<String>()

    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            binding.textIdentificacao.text = tag
            binding.layoutParent.setOnClickListener {
                onclick(tag)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeituraRfidEpcAdapterRfidVh {
        val binding =
            ItemRvEpcRfidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeituraRfidEpcAdapterRfidVh(binding)
    }

    override fun getItemCount() = listTags.size

    override fun onBindViewHolder(holder: LeituraRfidEpcAdapterRfidVh, position: Int) {
        holder.bind(listTags[position])
    }

    fun updateData(listNf: List<String>) {
        listTags.clear()
        listTags.addAll(listNf)
        notifyDataSetChanged()
    }

}