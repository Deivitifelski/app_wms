package com.documentos.wms_beirario.ui.mountingVol.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMounting4Binding
import com.documentos.wms_beirario.databinding.ItemRvMunting2AndressBinding
import com.documentos.wms_beirario.model.mountingVol.ResponseAndressMonting3Item
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting4
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting4Item

class AdapterMountingProd4() :
    ListAdapter<ResponseMounting4Item, AdapterMountingProd4.AdapterMountingProd4VH>(
        DiffVolMontingProd4()
    ) {

    inner class AdapterMountingProd4VH(val mBinding: ItemRvMounting4Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseMounting4Item?) {
            mBinding.apiSku.text = item?.SKU
            mBinding.apiEan.text = item?.EAN.toString()
            mBinding.apiQtdLidos.text = "${item?.quantidade}/${item?.quantidadeAdicionada}"
        }
    }

    fun searchItem(qrCode: String): ResponseMounting4Item? {
        return currentList.firstOrNull() {
            it.codigoBarras == qrCode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMountingProd4VH {
        val bind =
            ItemRvMounting4Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMountingProd4VH(bind)
    }

    override fun onBindViewHolder(holder: AdapterMountingProd4VH, position: Int) {
        holder.bind(getItem(position))
    }

}


class DiffVolMontingProd4() : DiffUtil.ItemCallback<ResponseMounting4Item>() {
    override fun areItemsTheSame(
        oldItem: ResponseMounting4Item,
        newItem: ResponseMounting4Item
    ): Boolean {
        return oldItem.EAN == newItem.EAN
                && oldItem.SKU == newItem.SKU
    }

    override fun areContentsTheSame(
        oldItem: ResponseMounting4Item,
        newItem: ResponseMounting4Item
    ): Boolean {
        return oldItem == newItem
    }

}
