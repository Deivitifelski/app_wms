package com.documentos.wms_beirario.ui.rfid_recebimento.blueBirdConect

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import co.kr.bluebird.sled.BTReader
import co.kr.bluebird.sled.SDConsts
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNfs.RfidRecebimentoActivity
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation


class BlueBirdBluetooth private constructor() {

    private var readerRfidBtn: BTReader? = null
    private val mainHandler = Handler(Looper.getMainLooper()) { res ->
        hendlerDeviceBluetooh(res)
        true
    }

    private fun hendlerDeviceBluetooh(res: Message) {

    }

    companion object {
        @Volatile
        private var instance: BlueBirdBluetooth? = null

        fun getInstance(): BlueBirdBluetooth {
            return instance ?: synchronized(this) {
                instance ?: BlueBirdBluetooth().also { instance = it }
            }
        }
    }


    fun openConect(
        context: Context,
        onOpenConect: (String) -> Unit,
        onReturnDevicesFound: () -> Unit
    ) {
        readerRfidBtn = BTReader.getReader(context, mainHandler)
        readerRfidBtn?.SD_Open()
        if (readerRfidBtn != null) {
            if (readerRfidBtn?.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
                onOpenConect("Você já esta conectado com o leitor:\n${readerRfidBtn?.BT_GetConnectedDeviceName()} - ${readerRfidBtn?.BT_GetConnectedDeviceAddr()}")
            } else {
                readerRfidBtn?.BT_StartScan()
            }
        }
    }
}
