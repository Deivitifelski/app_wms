package com.documentos.wms_beirario.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvVolumeInventoryBinding
import com.documentos.wms_beirario.model.inventario.VolumesResponseInventarioItem


class AdapterInventoryClickVolume() :
    ListAdapter<VolumesResponseInventarioItem, AdapterInventoryClickVolume.AdapterInventoryViewHolderVolume>(
        CallDiffUtilInventoryRvVolumes()
    ) {
    var listners: (VolumesResponseInventarioItem) -> Unit = {}

    inner class AdapterInventoryViewHolderVolume(private val mBinding: ItemRvVolumeInventoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(itemRv: VolumesResponseInventarioItem?) {
            if (itemRv!!.imprime != 0) {
                mBinding.itCountPosition.text = (adapterPosition + 1).toString()
                mBinding.itDocument.text = itemRv.numeroSerie
                mBinding.itImagePrinter.visibility = View.VISIBLE
            } else {
                mBinding.itCountPosition.text = (adapterPosition + 1).toString()
                mBinding.itDocument.text = itemRv.numeroSerie
                mBinding.itImagePrinter.visibility = View.INVISIBLE
            }
            mBinding.itImagePrinter.setOnClickListener {
                listners(itemRv)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterInventoryViewHolderVolume {
        val binding =
            ItemRvVolumeInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterInventoryViewHolderVolume(binding)
    }

    override fun onBindViewHolder(holder: AdapterInventoryViewHolderVolume, position: Int) {
        holder.bind(getItem(position))
    }
}

class CallDiffUtilInventoryRvVolumes : DiffUtil.ItemCallback<VolumesResponseInventarioItem>() {
    override fun areItemsTheSame(
        oldItem: VolumesResponseInventarioItem,
        newItem: VolumesResponseInventarioItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: VolumesResponseInventarioItem,
        newItem: VolumesResponseInventarioItem
    ): Boolean {
        return oldItem == newItem
    }

}
