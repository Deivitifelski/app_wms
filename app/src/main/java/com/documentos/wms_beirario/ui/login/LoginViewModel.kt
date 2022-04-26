package com.documentos.wms_beirario.ui.login

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.repository.login.LoginRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.*
import org.json.JSONObject

class LoginViewModel constructor(private val repository: LoginRepository) : ViewModel() {

    private val TAG = "LOGIN_VIEW_MODEL------->"
    private val _mLoginSucess = MutableLiveData<String>()
    val mLoginSucess: LiveData<String>
        get() = _mLoginSucess
    val mLoginErrorUser =  SingleLiveEvent<String>()
    val mLoginErrorServ = MutableLiveData<String>()
    val mValidaLogin = MutableLiveData<Boolean>()
    val mValidaButton = MutableLiveData<Boolean>()


    private fun registerUser(usuario: String, senha: String) {
        if (usuario.isEmpty() || usuario.isBlank()) {
            mValidaLogin.postValue(true)
        } else if (senha.isEmpty() || senha.isBlank()) {
            mValidaLogin.postValue(true)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val call = this@LoginViewModel.repository.postLogin(LoginRequest(usuario, senha))
                    if (call.isSuccessful) {
                        _mLoginSucess.postValue(call.body()!!.token)
                    } else {
                        withContext(Dispatchers.Main) {
                            val error = call.errorBody()!!.string()
                            val error2 = JSONObject(error).getString("message")
                            mLoginErrorUser.value = error2
                        }
                    }
                } catch (e: Exception )  {
                    withContext(Dispatchers.Main) {
                        mLoginErrorUser.value = "Ops...Erro inesperado!"
                    }
                }
            }
        }
    }

    fun getToken(usuario: String, senha: String): String {
        registerUser(usuario, senha)
        return _mLoginSucess.value.toString()
    }

    /** --------------------------------LoginViewModelFactory------------------------------------ */
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