package com.documentos.wms_beirario.ui.rfid_recebimento.localizationEpc

import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.documentos.wms_beirario.R

class TagProximityDialogFragment : DialogFragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var textRssiValue: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_tag_proximity, null)

        progressBar = view.findViewById(R.id.progressBarProximity)
        textRssiValue = view.findViewById(R.id.textRssiValue)

        dialog.setView(view)
            .setTitle("Proximidade da Tag")
            .setNegativeButton("Fechar") { _, _ -> dismiss() }

        return dialog.create()
    }

    fun updateProximity(rssi: Int) {
        // Converte o valor de RSSI para um percentual (ajuste conforme necessário)
        val proximityPercentage = calculateProximityPercentage(rssi)

        progressBar.progress = proximityPercentage
        textRssiValue.text = "RSSI: $rssi dBm"
    }

    private fun calculateProximityPercentage(rssi: Int): Int {
        return when {
            rssi >= -30 -> 100
            rssi in -30..-60 -> 75
            rssi in -60..-90 -> 50
            rssi < -90 -> 0
            else -> 0
        }
    }
}
