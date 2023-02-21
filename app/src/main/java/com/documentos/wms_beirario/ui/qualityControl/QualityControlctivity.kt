package com.documentos.wms_beirario.ui.qualityControl

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityQualityControlctivityBinding
import com.documentos.wms_beirario.model.qualityControl.*
import com.documentos.wms_beirario.repository.qualityControl.QualityControlRepository
import com.documentos.wms_beirario.ui.qualityControl.fragments.ApontedQualityFragment
import com.documentos.wms_beirario.ui.qualityControl.fragments.ApprovedQualityFragment
import com.documentos.wms_beirario.ui.qualityControl.fragments.NotApontedQualityFragment
import com.documentos.wms_beirario.ui.qualityControl.fragments.RejectedQualityFragment
import com.documentos.wms_beirario.ui.qualityControl.viewModel.QualityControlViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.clearEdit
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarrasString
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class QualityControlctivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityQualityControlctivityBinding
    private lateinit var mViewModel: QualityControlViewModel
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mListApontados = mutableListOf<Apontado>()
    private var mListNaoApontados = mutableListOf<NaoApontado>()
    private var mListAprovados = mutableListOf<Aprovado>()
    private var mListNaoAprovados = mutableListOf<NaoAprovado>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityQualityControlctivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initConst()
        setupEdit()
        setObserver()
        clickButtons()
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            this,
            QualityControlViewModel.QualityControlViewModelFactory1(QualityControlRepository())
        )[QualityControlViewModel::class.java]
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    //TIN00013339 - ARM 67
    private fun setupEdit() {
        mBinding.editQuality.extensionSetOnEnterExtensionCodBarrasString { codBarras ->
            if (codBarras.isNotEmpty()) {
                mViewModel.getTask1(codBarrasEnd = codBarras.trim())
            } else {
                vibrateExtension(500)
                mToast.toastCustomSucess(this, getString(R.string.edit_emply))
            }
        }
    }


    private fun clickButtons() {
        mBinding.buttonNaoApontados.setOnClickListener {
            replaceFragment(NotApontedQualityFragment(mListNaoApontados))
        }
        mBinding.buttonApontados.setOnClickListener {
            replaceFragment(ApontedQualityFragment(mListApontados))
        }
        mBinding.buttonGroupReceipt.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) when (checkedId) {
                R.id.button_rejeitado -> {
                    replaceFragment(RejectedQualityFragment(mListNaoAprovados))
                }
                R.id.button_aprovado -> {
                    replaceFragment(ApprovedQualityFragment(mListAprovados))
                }
            }
        }
    }


    //Respostas viewModel -->
    private fun setObserver() {
        mViewModel.apply {
            //Progress -->
            mProgressShow.observe(this@QualityControlctivity) { progress ->
                if (progress) mBinding.progressRec.visibility = View.VISIBLE
                else mBinding.progressRec.visibility = View.GONE
            }
            //Response sucesso 1 -->
            mSucessShow.observe(this@QualityControlctivity) { list ->
                clearEdit(mBinding.editQuality)
                if (list != null) {
                    setListas(list)
                    mBinding.editLayout.hint = "Leia um EAN"
                    setCout(list)

                }
            }

            //Erro Banco -->
            mErrorHttpShow.observe(this@QualityControlctivity) { error ->
                clearEdit(mBinding.editQuality)
                mAlert.alertMessageErrorSimplesAction(
                    this@QualityControlctivity,
                    error,
                    action = { clearEdit(mBinding.editQuality) })
            }
            //Error Geral -->
            mErrorAllShow.observe(this@QualityControlctivity) { error ->
                clearEdit(mBinding.editQuality)
                mAlert.alertMessageErrorSimples(this@QualityControlctivity, error)
            }
        }
    }

    //Cria as listas -->
    private fun setListas(list: ResponseQualityResponse1) {
        mListApontados.clear()
        mListNaoApontados.clear()
        mListAprovados.clear()
        mListNaoAprovados.clear()
        mListApontados.addAll(list.apontados)
        mListNaoApontados.addAll(list.naoApontados)
        mListAprovados.addAll(list.aprovados)
        mListNaoAprovados.addAll(list.naoAprovados)
    }

    //Replace dos fragmentos com s recycler views -->
    private fun replaceFragment(fragment: Fragment) {
        mBinding.progressFrame.visibility = View.VISIBLE
        Handler(Looper.myLooper()!!).postDelayed({
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_rv, fragment)
            fragmentTransaction.commit()
            mBinding.progressFrame.visibility = View.INVISIBLE
        }, 200)

    }

    //Seta a quantidade a ser mostrada nos buttons -->
    private fun setCout(list: ResponseQualityResponse1) {
        val apontados = list.apontados.size
        val naoApontados = list.naoApontados.size
        val aprovados = list.aprovados.size
        val rejeitados = list.naoAprovados.size
        mBinding.apply {
            buttonApontados.text = "Apontados - $apontados"
            buttonNaoApontados.text = "Não apontados - $naoApontados"
            buttonAprovado.text = "Aprovados - $aprovados"
            buttonRejeitado.text = "Rejeitados - $rejeitados"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }
}