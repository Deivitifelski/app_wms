package com.documentos.wms_beirario.ui.rfid_recebimento.bluetoohRfid

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvBluetoohRfiBinding
import com.documentos.wms_beirario.model.recebimentoRfid.bluetooh.BluetoohRfid

class BluetoohRfiBlueBirdSearchdAdapter(val onClickListener: (BluetoothDevice) -> Unit) :
    RecyclerView.Adapter<BluetoohRfiBlueBirdSearchdAdapter.BluetoohRfidAdapterVholder>() {


    private var listBluetooh = mutableListOf<BluetoohRfid>()

    inner class BluetoohRfidAdapterVholder(val binding: ItemRvBluetoohRfiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bluetooh: BluetoohRfid) {
            binding.txtBluetooh.text = "${bluetooh.name}\n${bluetooh.address}"

            itemView.setOnClickListener {
                bluetooh.device?.let { it1 -> onClickListener.invoke(it1) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoohRfidAdapterVholder {
        val l = ItemRvBluetoohRfiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BluetoohRfidAdapterVholder(l)
    }

    override fun getItemCount() = listBluetooh.size

    override fun onBindViewHolder(holder: BluetoohRfidAdapterVholder, position: Int) {
        holder.bind(listBluetooh[position])
    }

    fun updateList(bluetoohRfid: BluetoohRfid) {
        if (!listBluetooh.contains(bluetoohRfid)) {
            listBluetooh.add(bluetoohRfid)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        listBluetooh.clear()
        notifyDataSetChanged()
    }
}