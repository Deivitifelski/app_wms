import android.accounts.NetworkErrorException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import com.documentos.wms_beirario.ui.Tarefas.TipoTarefaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TipoTarefaViewModel(private val mRepository: TipoTarefaRepository) : ViewModel() {

    val mResponseSucess = MutableLiveData<List<TipoTarefaResponseItem>>()
    val mResponseError = MutableLiveData<String>()

    fun getTarefas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = this@TipoTarefaViewModel.mRepository.getTarefas()
                if (request.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        request.body().let { listTarefas ->
                            mResponseSucess.value =
                                listTarefas!!.filter { it.id != 2 && it.id != 8 }

                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val error = request.errorBody()!!.string()
                        val error2 = JSONObject(error).getString("message")
                        mResponseError.value = error2
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mResponseError.value = "Ops! Erro inesperado..."
                }
            }
        }
    }

    class TipoTarefaViewModelFactory constructor(private val repository: TipoTarefaRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(TipoTarefaViewModel::class.java)) {
                TipoTarefaViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}