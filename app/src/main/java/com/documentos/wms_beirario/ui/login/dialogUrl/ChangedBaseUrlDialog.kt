import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.DialogChangedBaeUrlBinding
import com.documentos.wms_beirario.utils.CustomSnackBarCustom


class ChangedBaseUrlDialog() : DialogFragment() {

    interface sendBase {
        fun sendBaseDialog(base: String, title: String)

    }

    private var mBinding: DialogChangedBaeUrlBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mInterface: sendBase
    private var baseChanged: String = ""
    private var title: String = ""
    private val mListRandom = mutableListOf<Int>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogChangedBaeUrlBinding.inflate(layoutInflater)
        dialog!!.setCancelable(false)
        mInterface = context as sendBase
        clickButtons()
        setPortDbCorrente()
        return binding.root
    }

    private fun setPortDbCorrente() {
        val sharedPreferences :CustomSharedPreferences = CustomSharedPreferences(requireContext())
        val banco = sharedPreferences.getString("TIPO_BANCO")
        when(banco){
            "HML" -> {
                mBinding!!.hml.isChecked = true
                baseChanged = "https://api-hml-internal.calcadosbeirario.com.br/coletor/wms/"
                title = getString(com.documentos.wms_beirario.R.string.development)
            }
            "Dev" -> {
                mBinding!!.dev.isChecked = true
                baseChanged = "https://api-dev-internal.calcadosbeirario.com.br/coletor/wms/"
                title = getString(com.documentos.wms_beirario.R.string.dev)
            }
            "PROD" -> {
                mBinding!!.prod.isChecked = true
                baseChanged = "https://api-prd-internal.calcadosbeirario.com.br/coletor/wms/"
                title = getString(com.documentos.wms_beirario.R.string.produce)
            }
            "LOCAL" -> {
                mBinding!!.localHost.isChecked = true
                baseChanged =
                    "https://ffe5-2804-14d-2ca2-83a3-a488-dd8e-b242-4867.ngrok-free.app/wms/"
                title = getString(com.documentos.wms_beirario.R.string.local_host)
            }
        }
    }


    private fun clickButtons() {
        initDados()
        mBinding!!.hml.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                baseChanged = "https://api-hml-internal.calcadosbeirario.com.br/coletor/wms/"
                title = getString(com.documentos.wms_beirario.R.string.development)
            }
        }

        mBinding!!.dev.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                baseChanged = "https://api-dev-internal.calcadosbeirario.com.br/coletor/wms/"
                title = getString(com.documentos.wms_beirario.R.string.dev)
            }
        }

        mBinding!!.prod.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                baseChanged = "https://api-prd-internal.calcadosbeirario.com.br/coletor/wms/"
                title = getString(com.documentos.wms_beirario.R.string.produce)
            }
        }

        //Local deve ser definada por exemplo pelo: ngrok http 3000
        mBinding!!.localHost.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                baseChanged =
                    "https://ffe5-2804-14d-2ca2-83a3-a488-dd8e-b242-4867.ngrok-free.app/wms/"
                title = getString(com.documentos.wms_beirario.R.string.local_host)
            }
        }

        mBinding!!.buttonOkUrl.setOnClickListener {
            mBinding!!.progressDialogLogin.isVisible = true
            mBinding!!.txtInfoDialogLogin.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                CustomSnackBarCustom().toastCustomSucess(requireContext(), title)
                mInterface.sendBaseDialog(baseChanged, title)
                mBinding!!.progressDialogLogin.isVisible = false
                mBinding!!.txtInfoDialogLogin.isVisible = false
                dismiss()
            }, mListRandom.random().toLong())
            Log.e("DIALOG_LOGIN", "${mListRandom.random()}")
        }
    }


    private fun initDados() {
        for (i in 500..1500) {
            mListRandom.add(i)
        }
        mBinding!!.prod.isChecked = true
        baseChanged = "http://10.0.1.111:5001/wms/"
        title = getString(com.documentos.wms_beirario.R.string.produce)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        mListRandom.clear()
    }
}