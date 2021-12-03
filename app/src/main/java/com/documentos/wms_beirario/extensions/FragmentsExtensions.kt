package com.documentos.wms_beirario.extensions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
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

fun Fragment.customReplaceFragment(@IdRes id: Int, fragment: Fragment) {
    if (requireActivity().supportFragmentManager.findFragmentById(id) == null) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            add(id, fragment)
                .setTransition(TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    } else {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(id, fragment)
                .addToBackStack(null)
                .setTransition(TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }
}

/**VOLTAR FRAGMENT ANTERIOR -->*/
fun Fragment.navBack() = findNavController().navigateUp()

/**VISIBILIDADE BUTTON -->*/
fun Fragment.buttonEnable(button: Button, visibility: Boolean) {
    button.isEnabled = visibility
}
