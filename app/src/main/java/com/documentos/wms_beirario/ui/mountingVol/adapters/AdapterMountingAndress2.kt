package com.documentos.wms_beirario.ui.mountingVol.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMunting2AndressBinding
import com.documentos.wms_beirario.model.mountingVol.ResponseAndressMonting3Item

class AdapterMountingAndress2() :
    ListAdapter<ResponseAndressMonting3Item, AdapterMountingAndress2.AdapterMountingAndress2VH>(
        DiffVolMontingAndress2()
    ) {

    inner class AdapterMountingAndress2VH(val mBinding: ItemRvMunting2AndressBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseAndressMonting3Item?) {
            mBinding.apiAndressMounting2.text = item?.enderecoVisual
            mBinding.apiProdMounting2.text = item?.quantidadeProdutos.toString()
        }
    }

    fun searchItem(qrCode: String): ResponseAndressMonting3Item? {
        return currentList.firstOrNull() {
            it.codigoBarras == qrCode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMountingAndress2VH {
        val bind =
            ItemRvMunting2AndressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMountingAndress2VH(bind)
    }

    override fun onBindViewHolder(holder: AdapterMountingAndress2VH, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffVolMontingAndress2() : DiffUtil.ItemCallback<ResponseAndressMonting3Item>() {
    override fun areItemsTheSame(
        oldItem: ResponseAndressMonting3Item,
        newItem: ResponseAndressMonting3Item
    ): Boolean {
        return oldItem.idEnderecoOrigem == newItem.idEnderecoOrigem
                && oldItem.codigoBarras == newItem.codigoBarras
    }

    override fun areContentsTheSame(
        oldItem: ResponseAndressMonting3Item,
        newItem: ResponseAndressMonting3Item
    ): Boolean {
        return oldItem == newItem
    }

}
