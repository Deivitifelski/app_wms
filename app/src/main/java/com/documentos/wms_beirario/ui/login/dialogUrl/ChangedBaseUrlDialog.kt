import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.documentos.wms_beirario.R
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
                mBaseChanged = "http://10.0.1.111:5002/wms/"
                mTitle = getString(R.string.development)
            }
        }
        mBinding!!.prod.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId) {
                mBaseChanged = "http://10.0.1.111:5001/wms/"
                mTitle = getString(R.string.produce)
            }
        }

        mBinding!!.buttonOkUrl.setOnClickListener {
            CustomSnackBarCustom().toastCustomSucess(requireContext(), "$mTitle")
            mInterface.sendBaseDialog(mBaseChanged, mTitle)
            dismiss()
        }
    }

    private fun initDados() {
        mBinding!!.prod.isChecked = true
        mBaseChanged = "http://10.0.1.111:5001/wms/"
        mTitle = getString(R.string.produce)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}