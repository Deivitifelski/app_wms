package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcResponse
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcs
import com.zebra.rfid.api3.TagData


class LeituraRfidAdapter(val onclick: (RecebimentoRfidEpcResponse) -> Unit) :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {


    private var listTags = mutableListOf<RecebimentoRfidEpcResponse>()

    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: RecebimentoRfidEpcResponse) {
            when(tag.status){
                "R" -> binding.layoutParent.setBackgroundResource(R.color.blue)
                "E" -> binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
                "N" -> binding.layoutParent.setBackgroundResource(R.color.red)
                "F" -> binding.layoutParent.setBackgroundResource(R.color.color_yelon_clear)
                else -> binding.layoutParent.setBackgroundResource(R.color.white)
            }
            binding.textIdentificacao.text = tag.numeroSerie
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

    fun updateData(listNf: List<RecebimentoRfidEpcResponse>) {
        listTags.clear()
        listTags.addAll(listNf)
        notifyDataSetChanged()
    }

    fun containsEpc(tagID: String) {
        listTags.find { it.numeroSerie == tagID }?.let { res ->
            res.status = "E"
            notifyDataSetChanged()
        }
    }

    fun returnListAll() = listTags
    fun filter(filter:String) {
        listTags.filter { it.status == filter }
        notifyDataSetChanged()
    }

}