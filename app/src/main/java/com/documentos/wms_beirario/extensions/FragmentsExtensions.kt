package com.documentos.wms_beirario.extensions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.InputMethod.SHOW_FORCED
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


/**VIBRAR -->*/
@Suppress("DEPRECATION")
        /** FAZ COM QUE O APARELHO VIBRE PELO TEMPO DEFINIDO: https://youtu.be/ogxgiaCq_24  */
fun Fragment.vibrate(duration: Long = 100) {
    val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val vm =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                vm.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    duration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
        else -> vibrator?.vibrate(duration)
    }
}


/**VOLTAR FRAGMENT ANTERIOR -->*/
fun Fragment.navBack() = findNavController().navigateUp()

/**EXIBE E ESCONDE TECLADO -->*/
