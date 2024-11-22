package com.documentos.wms_beirario.ui.rfid_recebimento.bluetoohRfid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvBluetoohRfiBinding
import com.documentos.wms_beirario.model.recebimentoRfid.bluetooh.BluetoohRfid


class BluetoohRfidPairedAdapter(val onClickListener: (BluetoohRfid) -> Unit) :
    RecyclerView.Adapter<BluetoohRfidPairedAdapter.BluetoohRfidPairedAdapterVh>() {


    private var listBluetooh = mutableListOf<BluetoohRfid>()

    inner class BluetoohRfidPairedAdapterVh(val binding: ItemRvBluetoohRfiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bluetooh: BluetoohRfid) {
            binding.txtBluetooh.text = "${bluetooh.name}\n${bluetooh.address}"

            itemView.setOnClickListener {
                onClickListener.invoke(bluetooh)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoohRfidPairedAdapterVh {
        val l = ItemRvBluetoohRfiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BluetoohRfidPairedAdapterVh(l)
    }

    override fun getItemCount() = listBluetooh.size

    override fun onBindViewHolder(holder: BluetoohRfidPairedAdapterVh, position: Int) {
        holder.bind(listBluetooh[position])
    }

    fun updateList(bluetoohRfid: BluetoohRfid) {
        if (!listBluetooh.contains(bluetoohRfid)) {
            listBluetooh.add(0, bluetoohRfid)
        }
        notifyDataSetChanged()
    }

    fun clear() {
        listBluetooh.clear()
        notifyDataSetChanged()
    }
}