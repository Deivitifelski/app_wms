package com.documentos.wms_beirario.utils.extensions

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.documentos.wms_beirario.BuildConfig
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeoutException

fun validaErrorException(e: Throwable): String {
    var error = ""
    when (e) {
        is ConnectException -> {
            error = "Erro na comunicação com banco"
        }

        is SocketException -> {
            error = "Verifique sua internet!"
        }

        is SocketTimeoutException -> {
            error = "Tempo de conexão excedido, tente novamente!"
        }

        is TimeoutException -> {
            error = "Tempo de conexão excedido, tente novamente!"
        }

        is InterruptedException -> {
            error = "Tempo de conexão excedido, tente novamente!"
        }

        is UnknownHostException -> {
            error = "Erro de comunicação com hostName"
        }

        else -> {
            error = e.toString()
        }
    }
    return error
}

fun Activity.returnNameVersionDb(): String {
    val sharedPreferences = CustomSharedPreferences(this)
    val name = sharedPreferences.getString(CustomSharedPreferences.NAME_USER) ?: ""
    val tipoDb = sharedPreferences.getString("TIPO_BANCO") ?: ""
    return "${getVersion()} | ${name.replace("_", " ").uppercase()} | $tipoDb"
}

fun <T> validaErrorDb(request: Response<T>): String {
    val error = request.errorBody()!!.string()
    val error2 = JSONObject(error).getString("message")
    return error2
}

fun Throwable.returnError(e: Throwable): String {
    var error = ""
    when (e) {
        is SocketException -> {
            error = "Verifique sua internet!"
        }

        is ConnectException -> {
            error = "Verifique sua internet!"
        }

        is SocketTimeoutException -> {
            error = "Tempo de conexão excedido, tente novamente!"
        }

        is TimeoutException -> {
            error = "Tempo de conexão excedido, tente novamente!"
        }

        else -> {
            error = e.toString()
        }
    }
    return error
}

fun Activity.extensionStartActivity(activity: Activity) {
    startActivity(Intent(this, activity::class.java))
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

/**
 * CLEAR EDITS -->
 */
fun Activity.clearEdit(editText: EditText) {
    editText.requestFocus()
    editText.setText("")
    editText.text.clear()
}

fun Activity.extensionBackActivityanimation(context: Context? = null) {
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun EditText.clickHideShowKey() {
    this.setOnClickListener {
        showKeyboard()
    }
}

fun String.extensionTrim() = this.trim()

fun Activity.extensionStarActivityanimation(context: Context) {
    CustomMediaSonsMp3().somClick(context)
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Activity.extensionVisibleProgress(progressBar: ProgressBar, visibility: Boolean) {
    progressBar.isVisible = visibility
}

fun Activity.extensionSendActivityanimation() {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
}

fun Activity.buttonEnable(button: Button, visibility: Boolean) {
    button.isEnabled = visibility
}

fun Activity.getVersion(): String {
    return BuildConfig.VERSION_NAME.split(" ")[0]
}

fun helloUser(): String {
    val sdf = SimpleDateFormat("HH")
    val currentDate = sdf.format(Date())
    Log.e("Hello user -->", currentDate)
    try {
        return when {
            currentDate.toString().toInt() in 0..12 -> {
                "Bom dia"
            }

            currentDate.toString().toInt() in 13..18 -> {
                "Boa tarde"
            }

            currentDate.toString().toInt() in 19..23 -> {
                "Boa noite"
            }

            else -> {
                "Olá"
            }
        }
    } catch (e: Exception) {
        return "Olá"
    }
}

fun Fragment.getVersion(): String {
    return BuildConfig.VERSION_NAME.split(" ")[0]
}

fun Activity.extensionStarBacktActivityChanged(activity: Activity) {
    CustomMediaSonsMp3().somClick(this)
    startActivity(Intent(this, activity::class.java))
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
}

fun View.extensionPulseAnimation(
    fromScale: Float = 1f,
    toScale: Float = .9f,
    duration: Long = 1500L
): View =
    apply {
        animate()
            .scaleX(toScale)
            .scaleY(toScale)
            .setDuration(duration)
            .withEndAction { extensionPulseAnimation(toScale, fromScale, duration) }
            .start()
    }

fun View.extensionSetExplodeAnimationClickListener(
    toScale: Float = 5f,
    startAction: () -> Unit = {},
    clickAction: () -> Unit
): View = apply {
    setOnClickListener {
        startAction()
        explodeAnimation(toScale = toScale, endAction = clickAction)
    }
}

//LEITURA COD.BARRAS -->
fun EditText.extensionSetOnEnterExtensionCodBarras(action: () -> Unit = {}) {
    setOnEditorActionListener { _, actionId, event ->
        return@setOnEditorActionListener when (actionId) {
            EditorInfo.IME_ACTION_SEND -> {
                action()
                true
            }

            else -> false
        }
    }
}

fun EditText.extensionSetOnEnterExtensionCodBarrasString(action: (String) -> Unit = {}) {
    setOnEditorActionListener { string, actionId, _ ->
        return@setOnEditorActionListener when (actionId) {
            EditorInfo.IME_ACTION_SEND -> {
                action(string.text.toString())
                true
            }

            else -> false
        }
    }
}

fun Activity.getVersionNameToolbar(description: String? = null): String {
    return try {
        val mSharedPreferences: CustomSharedPreferences = CustomSharedPreferences(this)
        val name = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER) ?: ""
        if (description != null) {
            "$description ${name.replace("_", " ")} | ${getVersion()}"
        } else {
            "${name.replace("_", " ")} | ${getVersion()}"
        }

    } catch (e: Exception) {
        getVersion()
    }
}

/**TOAST DE ERRO E SUCESSO -->*/
fun Activity.mErroToastExtension(context: Activity, msg: String) {
    vibrateExtension(500)
    CustomSnackBarCustom().toastCustomError(context, msg)
}


fun Activity.toastSucess(context: Activity, msg: String) {
    CustomSnackBarCustom().toastCustomSucess(context, msg)
    vibrateExtension(500)
}

fun View.explodeAnimation(toScale: Float = 5f, duration: Long = 150L, endAction: () -> Unit): View =
    apply {
        animate()
            .scaleX(toScale)
            .scaleY(toScale)
            .setDuration(duration)
            .withEndAction { endAction() }
            .start()
    }

fun View.extensionHideAnimation(toAlpha: Float = 0f, duration: Long = 50L): View = apply {
    animate()
        .alpha(toAlpha)
        .setDuration(duration)
        .start()
}

fun View.animateExplosionInverted(
    labelView: View,
    startAction: () -> Unit = {},
    clickAction: () -> Unit = {},
    explode: Float = 5f,
    startAlpha: Float = 0f,
    endAlpha: Float = 1f,
    scaleDown: Float = 0.3f,
    scaleUp: Float = 1f,
    scaleDownDuration: Long = 400L,
    scaleUpDuration: Long = 550L,
    textDuration: Long = 550L
): View = apply {
    setOnClickListener(null)
    scaleX = explode
    scaleY = explode
    labelView.alpha = startAlpha
    animate()
        .scaleX(scaleDown)
        .scaleY(scaleDown)
        .setDuration(scaleUpDuration)
        .withEndAction {
            kotlin.run {
                animate()
                    .scaleX(scaleUp)
                    .scaleY(scaleUp)
                    .setDuration(scaleDownDuration).withEndAction {
                        labelView.animate().alpha(endAlpha).setDuration(textDuration)
                            .withEndAction {
                                extensionPulseAnimation()
                                    .extensionSetExplodeAnimationClickListener(
                                        startAction = startAction,
                                        clickAction = clickAction,
                                    )
                            }.start()
                    }.start()
            }
        }.start()
}

fun TextInputLayout.shake(onEndAction: () -> Unit = {}) {
    val startX = 0f
    val translationX = 45f
    val bounceDuration = 1000L

    ObjectAnimator.ofFloat(
        this,
        "translationX",
        startX,
        translationX,
        startX
    ).apply {
        interpolator = BounceInterpolator()
        duration = bounceDuration
        start()
    }.doOnEnd { onEndAction() }
}


fun Activity.hideKeyExtensionActivity(editText: EditText) {
    editText.showSoftInputOnFocus = false
    editText.requestFocus()
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
}

//EXTENSÃO QUE DEPENDE DA VAR MOSTRA OU ESCONDE O TECLADO -->
fun EditText.clickShowKey(show: Boolean? = false) {
    if (show == true) {
        showKeyboard()
    } else {
        hideKeyboard()
    }
}

fun Activity.showKeyExtensionActivity(editText: EditText) {
    editText.showSoftInputOnFocus = true
    editText.requestFocus()
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
}

fun EditText.changedEditText(action: () -> Unit = {}) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            action()
        }

    })
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}