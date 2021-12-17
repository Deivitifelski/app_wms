package com.documentos.wms_beirario.ui.configuracoes.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ImpressorasItemBinding
import com.documentos.wms_beirario.databinding.ItemPrinterBinding

class ImpressorasListAdapter(
    private var devices: MutableList<BluetoothDevice>,
    private var listener: AdapterListener
) : RecyclerView.Adapter<ImpressorasListAdapter.ImpressorasViewHolder>() {
    interface AdapterListener {
        fun onClickDevice(device: BluetoothDevice)
    }

    class ImpressorasViewHolder(var mBInding: ItemPrinterBinding) :
        RecyclerView.ViewHolder(mBInding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImpressorasViewHolder {
        val inflater =
            ItemPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImpressorasViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ImpressorasViewHolder, position: Int) {
        if (devices != null && devices.size > 0) {
            val coluna = devices[position];
            holder.itemView.setOnClickListener {
                listener.onClickDevice(coluna)
            }
            holder.mBInding.tvImpressoraName.text = coluna.name
            holder.mBInding.tvImpressoraAddress.text = coluna.address
        } else {
            return;
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }
}