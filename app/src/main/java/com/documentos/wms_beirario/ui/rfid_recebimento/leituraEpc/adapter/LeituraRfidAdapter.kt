package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcs
import com.zebra.rfid.api3.TagData


class LeituraRfidAdapter(val onclick: (TagData) -> Unit) :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {


    private var listTags = mutableListOf<RecebimentoRfidEpcs>()

    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: RecebimentoRfidEpcs) {
            when(tag.state){
                "R" -> binding.layoutParent.setBackgroundResource(R.color.blue)
                "E" -> binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
                "N" -> binding.layoutParent.setBackgroundResource(R.color.red)
                "F" -> binding.layoutParent.setBackgroundResource(R.color.color_yelon_clear)
                else -> binding.layoutParent.setBackgroundResource(R.color.white)
            }
            binding.textIdentificacao.text = tag.tagData.tagID
            binding.layoutParent.setOnClickListener {
                onclick(tag.tagData)
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

    fun updateData(listNf: List<RecebimentoRfidEpcs>) {
        listTags.clear()
        listTags.addAll(listNf)
        notifyDataSetChanged()
    }

    fun containsEpc(tagID: String) {
        listTags.find { it.tagData.tagID == tagID }?.let { res ->
            res.state = "E"
            notifyDataSetChanged()
        }
    }

    fun filter(filter:String) {
        listTags.filter { it.state == filter }
        notifyDataSetChanged()
    }

}