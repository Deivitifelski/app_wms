import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess
import com.documentos.wms_beirario.model.inventario.ResponseQrCode2
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class InventoryReadingViewModel2(private val repository1: InventoryoRepository1) : ViewModel() {


    private var mValidadTxt = MutableLiveData<Boolean>()
    val mValidadTxtShow: LiveData<Boolean>
        get() = mValidadTxt

    //----------->
//MutableLiveData<com.documentos.appwmsbeirario.model.inventario.com.documentos.wms_beirario.model.inventario.ResponseQrCode2>()
    private var mSucessComparation2 = MutableLiveData<ResponseQrCode2>()
    val mSucessComparationShow2: LiveData<ResponseQrCode2>
        get() = mSucessComparation2


    private var mSucess = MutableLiveData<ResponseQrCode2>()
    val mSucessShow: LiveData<ResponseQrCode2>
        get() = mSucess

    //----------->
    private var mError = SingleLiveEvent<String>()
    val mErrorShow: LiveData<String>
        get() = mError

    //----------->
    private var mErrorAll = SingleLiveEvent<String>()
    val mErrorAllShow: LiveData<String>
        get() = mErrorAll

    //----------->
    private var mValidaProgress = MutableLiveData<Boolean>()
    val mValidaProgressShow: LiveData<Boolean>
        get() = mValidaProgress

    fun readingQrCode(inventoryReadingProcess: RequestInventoryReadingProcess) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request = this@InventoryReadingViewModel2.repository1.inventoryQrCode2(
                    inventoryReadingProcess = inventoryReadingProcess
                )
                if (request.isSuccessful) {
                    mSucess.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorAll.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorAll.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    fun readingQrCodeDialog(inventoryReadingProcess: RequestInventoryReadingProcess) {
        viewModelScope.launch {
            try {
                mValidaProgress.postValue(true)
                val request = this@InventoryReadingViewModel2.repository1.inventoryQrCode2(
                    inventoryReadingProcess = inventoryReadingProcess
                )
                if (request.isSuccessful) {
                    mSucessComparation2.postValue(request.body())
                } else {
                    val error = request.errorBody()!!.string()
                    val error2 = JSONObject(error).getString("message")
                    val message = error2.replace("nao", "não").replace("CODIGO", "CÓDIGO")
                        .replace("INVALIDO", "INVÁLIDO")
                    mError.postValue(message)
                }

            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> {
                        mErrorAll.postValue("Verifique sua internet!")
                    }
                    is SocketTimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    is TimeoutException -> {
                        mErrorAll.postValue("Tempo de conexão excedido, tente novamente!")
                    }
                    else -> {
                        mErrorAll.postValue(e.toString())
                    }
                }
            } finally {
                mValidaProgress.postValue(false)
            }
        }
    }

    /** --------------------------------INVENTARIO ViewModelFactory------------------------------------ */
    class ReadingFragiewModelFactory constructor(private val repository: InventoryoRepository1) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(InventoryReadingViewModel2::class.java)) {
                InventoryReadingViewModel2(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}