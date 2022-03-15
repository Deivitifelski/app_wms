package com.documentos.wms_beirario.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvTamanhoShowInventarioBinding


class AdapterInventorySelectNum(private val onClick: (Int,Int) -> Unit) :
    RecyclerView.Adapter<AdapterInventorySelectNum.AdapterInventorySelectNumViewHolder>() {

    var mList = mutableListOf<Int>()

    inner class AdapterInventorySelectNumViewHolder(private val mBinding: ItemRvTamanhoShowInventarioBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(numShoes: Int) {
            mBinding.txtApiTam.text = numShoes.toString()
            itemView.setOnClickListener {
                onClick.invoke(numShoes,bindingAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterInventorySelectNumViewHolder {
        val binding = ItemRvTamanhoShowInventarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterInventorySelectNumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterInventorySelectNumViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun update(list: List<Int>, mPosition: Int) {
        mList.addAll(list)
        notifyDataSetChanged()
    }
}

