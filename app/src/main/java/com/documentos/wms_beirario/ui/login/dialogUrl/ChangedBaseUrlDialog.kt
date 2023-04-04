import android.os.Build
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.resources.R
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.documentos.wms_beirario.databinding.DialogChangedBaeUrlBinding
import com.documentos.wms_beirario.utils.CustomSnackBarCustom


class ChangedBaseUrlDialog() : DialogFragment() {

    interface sendBase {
        fun sendBaseDialog(base: String, title: String)

    }

    private var mBinding: DialogChangedBaeUrlBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mInterface: sendBase
    private var mBaseChanged: String = ""
    private var mTitle: String = ""
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
        return binding.root
    }


    private fun clickButtons() {
        initDados()
        mBinding!!.dev.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                mBaseChanged = "http://srvcol-hml.beirario.intranet:5002/wms"
                mTitle = getString(com.documentos.wms_beirario.R.string.development)
            }
        }

        mBinding!!.prod.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                mBaseChanged = "http://srvcol.beirario.intranet:5001/wms"
                mTitle = getString(com.documentos.wms_beirario.R.string.produce)
            }
        }

        mBinding!!.localHost.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                mBaseChanged =
                    "https://3a68-2804-14d-2ca2-8096-c849-3ea8-a121-8dfd.ngrok.io/wms/"
                mTitle = getString(com.documentos.wms_beirario.R.string.local_host)
            }
        }

        mBinding!!.buttonOkUrl.setOnClickListener {
            mBinding!!.progressDialogLogin.isVisible = true
            mBinding!!.txtInfoDialogLogin.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                CustomSnackBarCustom().toastCustomSucess(requireContext(), mTitle)
                mInterface.sendBaseDialog(mBaseChanged, mTitle)
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
        mBaseChanged = "http://10.0.1.111:5001/wms/"
        mTitle = getString(com.documentos.wms_beirario.R.string.produce)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        mListRandom.clear()
    }
}