package com.documentos.wms_beirario.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.login.LoginRequest
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.ConnectException

class LoginViewModel constructor(private val repository: LoginRepository) : ViewModel() {

    private val TAG = "LOGIN_VIEW_MODEL------->"
    val mLoginSucess = MutableLiveData<String>()
    val mLoginErrorUser = MutableLiveData<String>()
    val mLoginErrorServ = MutableLiveData<String>()
    val mValidaLogin = MutableLiveData<Boolean>()


    fun registerUser(usuario: String, senha: String) {
        if (usuario.isEmpty() || usuario.isBlank()) {
            mValidaLogin.postValue(true)
        } else if (senha.isEmpty() || senha.isBlank()) {
            mValidaLogin.postValue(true)
        } else {

            viewModelScope.launch(Dispatchers.IO){
                try {
                    Log.e(TAG, Thread.currentThread().name)
                    val call =
                        this@LoginViewModel.repository.postLogin(LoginRequest(usuario, senha))
                    if (call.isSuccessful) {
                        mLoginSucess.postValue(call.body()!!.token)
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e(TAG, Thread.currentThread().name)
                            val error = call.errorBody()!!.string()
                            val error2 = JSONObject(error).getString("message")
                            mLoginErrorUser.value = error2
                        }
                    }
                } catch (e:Exception){
                    withContext(Dispatchers.Main) {
                        mLoginErrorUser.value = "Ops...Erro inesperado!"
                    }
                }
            }
        }
    }

    fun getToken(usuario: String, senha: String): String {
        Log.e(TAG,Thread.currentThread().name)
        registerUser(usuario, senha)
        return mLoginSucess.value.toString()
    }

    class LoginViewModelFactory constructor(private val repository: LoginRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                LoginViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }


}