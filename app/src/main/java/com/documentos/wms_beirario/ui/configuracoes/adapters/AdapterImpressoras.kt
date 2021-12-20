package com.documentos.wms_beirario.ui.configuracoes.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemPrinterBinding

class AdapterImpressoras(
    private var onClick: (BluetoothDevice) -> Unit
) : ListAdapter<BluetoothDevice, AdapterImpressoras.ImpressorasViewHolder>(DiffUtilPrinter()) {

    inner class ImpressorasViewHolder(var mBInding: ItemPrinterBinding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun bind(devices: BluetoothDevice) {
            mBInding.tvImpressoraName.text = devices.name
            mBInding.tvImpressoraAddress.text = devices.address
            itemView.setOnClickListener {
                onClick.invoke(devices)
            }

        }

    }

    class DiffUtilPrinter :DiffUtil.ItemCallback<BluetoothDevice>(){
        override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(
            oldItem: BluetoothDevice,
            newItem: BluetoothDevice
        ): Boolean {
           return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImpressorasViewHolder {
        val inflater =
            ItemPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImpressorasViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ImpressorasViewHolder, position: Int) {

        holder.bind(getItem(position))
    }


    override fun getItemViewType(position: Int): Int {
        return position

    }


}


