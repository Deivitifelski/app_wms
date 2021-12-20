package com.documentos.wms_beirario.ui.configuracoes

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityBluetoohYoutubeBinding
import com.documentos.wms_beirario.ui.configuracoes.adapters.AdapterImpressoras
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.example.coletorwms.constants.CustomSnackBarCustom

class BluetoohYoutubeActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBluetoohYoutubeBinding
    private var mBluetoohAdapter: BluetoothAdapter? = null
    private lateinit var mAdapter: AdapterImpressoras
    private lateinit var mListDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOH = 1

    companion object {
        val EXTRA_ANDRESS: String = "device_andress"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBluetoohYoutubeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        AppExtensions.visibilityProgressBar(mBinding.progressBar2, visibility = false)
        setupRecyclerView()
        setupToolbar()
        //VERIFICA SE BLUETTOH E COMPATIVEL COM DISPOSITIVO -->
        mBluetoohAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoohAdapter == null) {
            CustomSnackBarCustom().snackBarErrorAction(
                mBinding.root,
                getString(R.string.support_bluetooth)
            )
            return
        }
        //VERIFICA SE BLUETOOH ESTA LIGADO,CASO ESTEJA DES.LIGAR ->
        if (mBluetoohAdapter!!.isEnabled) {
            val enableBluetooh = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooh, REQUEST_ENABLE_BLUETOOH)
        }
        mBinding.buttonUpdate.setOnClickListener {
            AppExtensions.visibilityProgressBar(mBinding.progressBar2, visibility = true)
            Handler().postDelayed({
                AppExtensions.visibilityProgressBar(mBinding.progressBar2, visibility = false)
                refleshDevices()
            }, 1500)
        }

    }

    private fun setupRecyclerView() {
        mAdapter = AdapterImpressoras { itemClick ->
            Toast.makeText(this, itemClick.name.toString(), Toast.LENGTH_SHORT).show()
        }
        mBinding.listDevicesYoutube.apply {
            layoutManager = LinearLayoutManager(this@BluetoohYoutubeActivity)
            adapter = mAdapter
        }
    }

    private fun setupToolbar() {
        mBinding.toolbar.setNavigationOnClickListener {
            onBackTransition()
        }
    }

    private fun refleshDevices() {
        BluetoothAdapter.ACTION_DISCOVERY_STARTED
        mListDevices = mBluetoohAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if (mListDevices.isNotEmpty()) {
            for (devices: BluetoothDevice in mListDevices) {
                list.add(devices)
                Log.e("BLUETOOH -->", devices.address)
            }
        } else {
            Toast.makeText(this, getString(R.string.unable_to_connect), Toast.LENGTH_SHORT).show()
        }
        mAdapter.submitList(list)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOH) {
            if (resultCode == Activity.RESULT_OK) {
                if (mBluetoohAdapter!!.isEnabled) {
                    Toast.makeText(this, getString(R.string.Bluetooth_Enabled), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.Bluetooth_no_Enabled),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this,
                    getString(R.string.Bluetooth_connection_canceled),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}