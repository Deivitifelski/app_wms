package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding
import com.zebra.rfid.api3.TagData


class LeituraRfidAdapter(val onclick: (TagData) -> Unit) :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {


    private var listTags = mutableListOf<TagData>()

    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: TagData) {
            binding.textIdentificacao.text = tag.tagID.toString()
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

    fun updateData(listNf: List<TagData>) {
        listTags.clear()
        listTags.addAll(listNf)
        notifyDataSetChanged()
    }

}