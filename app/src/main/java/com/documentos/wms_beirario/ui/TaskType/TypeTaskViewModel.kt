import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import com.documentos.wms_beirario.ui.TaskType.TypeTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException

class TypeTaskViewModel(private val mRepository: TypeTaskRepository) : ViewModel() {

    val mResponseSucess = MutableLiveData<List<TipoTarefaResponseItem>>()
    val mResponseError = MutableLiveData<String>()

    fun getTarefas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = this@TypeTaskViewModel.mRepository.getTarefas()
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
            }catch (connect :ConnectException){

            }
        }
    }

}