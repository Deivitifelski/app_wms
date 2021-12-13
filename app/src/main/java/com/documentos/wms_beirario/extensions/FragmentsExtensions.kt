package com.documentos.wms_beirario.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.constraintlayout.helper.widget.Carousel
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**VIBRAR -->*/
@Suppress("DEPRECATION")
        /** FAZ COM QUE O APARELHO VIBRE PELO TEMPO DEFINIDO: https://youtu.be/ogxgiaCq_24  */
fun Fragment.vibrateExtension(duration: Long = 100) {
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

/**VIBRAR -->*/
@Suppress("DEPRECATION")
fun Activity.vibrateExtension(duration: Long = 100) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val vm =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
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

fun  Fragment.hideKeyExtension(editText : EditText){
    editText.showSoftInputOnFocus = false
    editText.requestFocus()
    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
}

