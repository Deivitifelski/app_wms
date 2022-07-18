package com.documentos.wms_beirario.ui.mountingVol.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvMounting2VolumeBinding
import com.documentos.wms_beirario.model.mountingVol.ResponseMounting2Item

class AdapterMountingVol2() :
    ListAdapter<ResponseMounting2Item, AdapterMountingVol2.AdapterMountingVol2VH>(
        DiffVolMonting2()
    ) {

    var clickPrinter: (ResponseMounting2Item) -> Unit = {}

    inner class AdapterMountingVol2VH(val mBinding: ItemRvMounting2VolumeBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseMounting2Item?) {
            mBinding.apiVolMounting2.text = item?.numeroSerie
            //CLICK NA IMAGEM PARA IMPRIMIR -->
            mBinding.printerMounting2.setOnClickListener {
                if (item != null) {
                    clickPrinter(item)
                }
            }
        }
    }

    fun searchItem(qrCode: String): ResponseMounting2Item? {
        return currentList.firstOrNull() {
            it.numeroSerie == qrCode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMountingVol2VH {
        val bind =
            ItemRvMounting2VolumeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMountingVol2VH(bind)
    }

    override fun onBindViewHolder(holder: AdapterMountingVol2VH, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffVolMonting2() : DiffUtil.ItemCallback<ResponseMounting2Item>() {
    override fun areItemsTheSame(
        oldItem: ResponseMounting2Item,
        newItem: ResponseMounting2Item
    ): Boolean {
        return oldItem.numeroSerie == newItem.numeroSerie
    }

    override fun areContentsTheSame(
        oldItem: ResponseMounting2Item,
        newItem: ResponseMounting2Item
    ): Boolean {
        return oldItem == newItem
    }

}
