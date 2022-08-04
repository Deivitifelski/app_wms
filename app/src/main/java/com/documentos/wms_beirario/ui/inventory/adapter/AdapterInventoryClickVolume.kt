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
                with(mBinding) {
                    itCountPosition.text = (adapterPosition + 1).toString()
                    itDocument.text = itemRv.numeroSerie
                    itImagePrinter.visibility = View.VISIBLE
                    itGrade.text = itemRv.codigoGrade.toString()
                    if (itemRv.sku.length > 21) {
                        val newText = itemRv.sku.subSequence(0, 21)
                        val lastText = itemRv.sku.subSequence(21, itemRv.sku.length - 1)
                        itSku.text = "$newText\n$lastText"
                    } else {
                        itSku.text = itemRv.sku
                    }
                }
            } else {
                with(mBinding) {
                    itCountPosition.text = (adapterPosition + 1).toString()
                    itDocument.text = itemRv.numeroSerie
                    itImagePrinter.visibility = View.INVISIBLE
                    itGrade.text = itemRv.codigoGrade.toString()
                    if (itemRv.sku.length > 21) {
                        val newText = itemRv.sku.subSequence(0, 21)
                        val lastText = itemRv.sku.subSequence(21, itemRv.sku.length - 1)
                        itSku.text = "$newText\n$lastText"
                    } else {
                        itSku.text = itemRv.sku
                    }
                }
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
