package com.documentos.wms_beirario.utils.extensions

import android.app.Activity
import android.content.Context
import android.os.*
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.RawRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.Delay
import java.util.concurrent.Delayed


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


fun DialogFragment.clearEdit(editText: EditText) {
    editText.requestFocus()
    editText.setText("")
    editText.text.clear()
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

fun Fragment.visibilityLottieExtend(image: LottieAnimationView, visibility: Boolean = true){
     if (visibility) image.visibility = View.VISIBLE else image.visibility = View.INVISIBLE
}

fun  Fragment.hideKeyExtensionFragment(editText : EditText){
    editText.showSoftInputOnFocus = false
    editText.requestFocus()
    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
}
