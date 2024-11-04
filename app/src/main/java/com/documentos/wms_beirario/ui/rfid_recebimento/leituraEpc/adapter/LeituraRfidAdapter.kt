package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfidEpcResponse


class LeituraRfidAdapter(val onclick: (RecebimentoRfidEpcResponse) -> Unit) :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {


    private var listTags = mutableListOf<RecebimentoRfidEpcResponse>()

    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: RecebimentoRfidEpcResponse) {
            when (tag.status) {
                "R" -> binding.layoutParent.setBackgroundResource(R.color.blue)
                "E" -> binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
                "N" -> binding.layoutParent.setBackgroundResource(R.color.red)
                "F" -> binding.layoutParent.setBackgroundResource(R.color.color_yelon_clear)
                else -> binding.layoutParent.setBackgroundResource(R.color.white)
            }
            binding.textIdentificacao.text = tag.numeroSerie
            if (tag.descricaoCorCdgo != null && tag.descricaoCorCdgo.length >= 40) {
                binding.textCor.text =
                    "Cor: ${tag.corCdgo ?: "-"} | ${tag.descricaoCorCdgo.substring(0, 40)}"
            } else {
                binding.textCor.text = "Cor: ${tag.corCdgo ?: "-"} | ${tag.descricaoCorCdgo}"
            }

            if (tag.descricaoIesCodigo != null && tag.descricaoIesCodigo.length >= 40) {
                binding.textIesDes.text = "Desc: ${tag.descricaoIesCodigo.substring(0, 40)}"
            } else {
                binding.textIesDes.text = "Desc: ${tag.descricaoIesCodigo}"
            }

            binding.textNf.text = "Nf: ${tag.notaFiscal}"

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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateData(listNf: List<RecebimentoRfidEpcResponse>) {
        listTags.clear()
        listTags.addAll(listNf)
        notifyDataSetChanged()

    }
}