package com.documentos.wms_beirario.ui.desmontagemdevolumes.vielmodel

import androidx.lifecycle.*
import com.documentos.wms_beirario.model.desmontagemdevolumes.DisassemblyResponse1
import com.documentos.wms_beirario.repository.desmontagemvolumes.DisassemblyRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class DisassemblyViewModel1(private val mDisassemblyRepository: DisassemblyRepository) :
    ViewModel() {
    private var mSucesss = MutableLiveData<List<DisassemblyResponse1>>()
    val mSucessShow: LiveData<List<DisassemblyResponse1>>
        get() = mSucesss

    //----------->
    private var mError = MutableLiveData<String>()
    val mErrorShow: LiveData<String>
        get() = mError
    //----------->
    private var mValidProgress = MutableLiveData<Boolean>()
    val mValidProgressShow : LiveData<Boolean>
    get() = mValidProgress

    fun getDisassembly1() {
        viewModelScope.launch {
            try {
                val request = this@DisassemblyViewModel1.mDisassemblyRepository.getDisassembly1()
                mValidProgress.value = false
                if (request.isSuccessful) {
                    request.let { listLet ->
                        mSucesss.postValue(listLet.body())
                    }
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)

                }
            } catch (e: Exception) {
                mValidProgress.value = false
                mError.postValue("Ops! Erro inesperado...")

            }
        }
    }
}