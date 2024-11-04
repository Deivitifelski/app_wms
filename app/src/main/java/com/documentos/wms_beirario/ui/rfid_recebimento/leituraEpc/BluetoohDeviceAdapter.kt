package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class BluetoothDeviceAdapter(context: Context, devices: MutableList<BluetoothDevice>) :
    ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_2, devices) {

    private var deviceList = devices

    fun updateDevices(newDevices: List<BluetoothDevice>) {
        deviceList.clear()
        deviceList.addAll(newDevices)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return deviceList.size
    }

    override fun getItem(position: Int): BluetoothDevice? {
        return deviceList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = device?.name ?: "Dispositivo desconhecido"
        text2.text = device?.address

        return view
    }
}
