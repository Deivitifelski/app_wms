package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEpcRfidBinding


class LeituraRfidAdapter(val onclick: () -> Unit) :
    RecyclerView.Adapter<LeituraRfidAdapter.LeituraRfidEpcAdapterRfidVh>() {


    inner class LeituraRfidEpcAdapterRfidVh(val binding: ItemRvEpcRfidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
//            when (item.tipoLeitura) {
//                "E" -> {
//                    binding.layoutParent.setBackgroundResource(R.color.green_verde_clear)
//                }
//
//                "F" -> {
//                    binding.layoutParent.setBackgroundResource(R.color.color_yelon_clear)
//                }
//
//                "R" -> {
//                    binding.layoutParent.setBackgroundResource(R.color.blue)
//                }
//
//                "NR" -> {
//                    binding.layoutParent.setBackgroundResource(R.color.red)
//                }
//            }

//            binding.textIdentificacao.text = item.tag
//            binding.textInfo.text = item.descricao

            //Clique no item
            binding.layoutParent.setOnClickListener {
                onclick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeituraRfidEpcAdapterRfidVh {
        val binding =
            ItemRvEpcRfidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeituraRfidEpcAdapterRfidVh(binding)
    }

    override fun getItemCount() = 0

    override fun onBindViewHolder(holder: LeituraRfidEpcAdapterRfidVh, position: Int) {
//        holder.bind(list[position])
    }

//    fun updateData(listNf: List<LeituraRfidEpc>) {
//        list.clear()
//        list.addAll(listNf)
//        notifyDataSetChanged()
//    }

}