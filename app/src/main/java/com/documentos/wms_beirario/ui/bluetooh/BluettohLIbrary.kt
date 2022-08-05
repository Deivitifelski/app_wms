package com.documentos.wms_beirario.ui.bluetooh

import android.R
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.documentos.wms_beirario.databinding.ActivityBluetoohPrinterBinding
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.utils.extensions.mSucessToastExtension

class BluettohLIbrary : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohPrinterBinding
    private var device: String = ""
    private lateinit var listView: ListView
    private val mDeviceList = ArrayList<BleDevice>()
    private val mDeviceListShow = ArrayList<String>()
    private lateinit var mDeviceConnect: BleDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityBluetoohPrinterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        BleManager.getInstance().init(application)
        chekBLue()
        initBluetooh()
        scan()
        listView = mBinding.listView

    }

    private fun initBluetooh() {
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setSplitWriteNum(20)
            .setConnectOverTime(10000).operateTimeout = 5000;
    }

    private fun chekBLue() {
        if (BleManager.getInstance().isSupportBle) {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Não suporta BLuetooh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scan() {
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Toast.makeText(this@BluettohLIbrary, "Iniciando", Toast.LENGTH_SHORT).show()
            }

            override fun onScanning(bleDevice: BleDevice?) {
                if (bleDevice != null) {
                    mDeviceList.add(bleDevice)
                }
                mDeviceListShow.add("${bleDevice?.device?.name} \n ${bleDevice?.mac}")
                listView.adapter = ArrayAdapter(
                    this@BluettohLIbrary,
                    R.layout.simple_list_item_1, mDeviceListShow
                )
                listView.setOnItemClickListener { _, _, position, _ ->
                    mDeviceConnect = mDeviceList[position]
                    conect(mDeviceConnect)
                    mDeviceListShow
                    SetupNamePrinter.mNamePrinterString = mDeviceListShow[position]
                }
            }

            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                mBinding.progress.isVisible = false
            }
        })
    }


    private fun conect(bleDevice: BleDevice) {
        BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
            override fun onStartConnect() {

                mSucessToastExtension(
                    this@BluettohLIbrary,
                    "Impressora conectada: ${bleDevice.device?.address}"
                )
                onBackPressed()
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {

            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                mSucessToastExtension(
                    this@BluettohLIbrary,
                    "Impressora conectada: ${bleDevice?.device?.address}"
                )
                onBackPressed()
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {

            }
        });
    }

}