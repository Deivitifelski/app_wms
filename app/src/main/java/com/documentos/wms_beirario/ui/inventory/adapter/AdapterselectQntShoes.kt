package com.documentos.wms_beirario.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSelectTamShoesBinding
import org.koin.core.parameter.parametersOf


class AdapterselectQntShoes(private val onClick: (Int) -> Unit) :
    RecyclerView.Adapter<AdapterselectQntShoes.AdapterInventorySelectAlertNumViewHolder>() {

   private  var mList = mutableListOf<Int>()



    inner class AdapterInventorySelectAlertNumViewHolder(private val mBinding: ItemRvSelectTamShoesBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(numShoes: Int) {
            mBinding.itNumShoes.text = numShoes.toString()
            itemView.setOnClickListener {
                onClick.invoke(numShoes)
            }
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterInventorySelectAlertNumViewHolder {
        val binding = ItemRvSelectTamShoesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterInventorySelectAlertNumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterInventorySelectAlertNumViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun updateAlertDialog(retornandolist: List<Int>) {
        mList.addAll(retornandolist)
        notifyDataSetChanged()

    }
}

