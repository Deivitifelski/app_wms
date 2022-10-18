package com.documentos.wms_beirario.ui.login

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.login.LoginRequest
import com.documentos.wms_beirario.repository.login.LoginRepository
import com.documentos.wms_beirario.utils.SingleLiveEvent
import com.documentos.wms_beirario.utils.extensions.validaErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val TAG = "LOGIN_VIEW_MODEL------->"
    private val _mLoginSucess = SingleLiveEvent<String>()
    val mLoginSucess: SingleLiveEvent<String>
        get() = _mLoginSucess

    //------->
    private val mErrorAll = SingleLiveEvent<String>()
    val mErrorAllShow: SingleLiveEvent<String>
        get() = mErrorAll

    //------->
    private val mProgress = MutableLiveData<Boolean>()
    val mProgressShow: LiveData<Boolean>
        get() = mProgress

    val mLoginErrorUser = SingleLiveEvent<String>()
    val mLoginErrorServ = MutableLiveData<String>()


    private fun registerUser(usuario: String, senha: String) {
        if (usuario.isEmpty() || usuario.isBlank()) {
            mLoginErrorUser.postValue("Preencha todos os campos!")
        } else if (senha.isEmpty() || senha.isBlank()) {
            mLoginErrorUser.postValue("Preencha todos os campos!")
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    mProgress.postValue(true)
                    val call =
                        this@LoginViewModel.repository.postLogin(LoginRequest(usuario, senha))
                    if (call.isSuccessful) {
                        call.let {
                            _mLoginSucess.postValue(it.body()!!.token)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            val error = call.errorBody()!!.string()
                            val error2 = JSONObject(error).getString("message")
                            mLoginErrorUser.value = error2
                        }
                    }
                } catch (e: Exception) {
                    mErrorAll.postValue(validaErrorException(e))
                } finally {
                    mProgress.postValue(false)
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