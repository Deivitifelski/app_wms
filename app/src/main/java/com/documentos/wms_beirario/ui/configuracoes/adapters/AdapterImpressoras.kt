package com.documentos.wms_beirario.ui.configuracoes.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemPrinterBinding

class AdapterImpressoras(
    private var devices: MutableList<BluetoothDevice>,
    private var listener: AdapterListener
) : RecyclerView.Adapter<AdapterImpressoras.ImpressorasViewHolder>() {
    interface AdapterListener {
        fun onClickDevice(device: BluetoothDevice)
    }

    inner class ImpressorasViewHolder(var mBinding: ItemPrinterBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(bluetoothDevice: BluetoothDevice) {
            mBinding.tvImpressoraName.text = bluetoothDevice.name
            mBinding.tvImpressoraAddress.text = bluetoothDevice.address
            itemView.setOnClickListener {
                listener.onClickDevice(bluetoothDevice)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImpressorasViewHolder {
        val inflater =
            ItemPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImpressorasViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ImpressorasViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount() = devices.size

}