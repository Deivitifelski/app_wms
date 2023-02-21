package com.documentos.wms_beirario.ui.qualityControl

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
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
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class QualityControlctivity : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityQualityControlctivityBinding
    private lateinit var mViewModel: QualityControlViewModel
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
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
        initDataWedge()
        setupDataWedge()
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
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
        mBinding.editQuality.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editQuality.text.toString().isNotEmpty()) {
                mViewModel.getTask1(codBarrasEnd = mBinding.editQuality.text.toString().trim())
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
        //BUTTON REJEITADO -->
        mBinding.buttonRejeitado.setOnClickListener {
            mBinding.buttonRejeitado.apply {
                setTextColor(Color.parseColor("#FFFFFFFF"))
                setBackgroundResource(R.drawable.button_right_red_quality_control) //VERMELHO DEFAULT
            }
            mBinding.buttonAprovado.apply {
                setTextColor(Color.parseColor("#80000000")) //BLACK 15
                setBackgroundResource(R.drawable.button_left_quality_control) //Verde
            }
            replaceFragment(RejectedQualityFragment(mListNaoAprovados))
        }

        //BUTON APROVADO -->
        mBinding.buttonAprovado.setOnClickListener {
            mBinding.buttonRejeitado.apply {
                setTextColor(Color.parseColor("#80000000"))
                setBackgroundResource(R.drawable.button_right_quality_control) //BRANCO
            }
            mBinding.buttonAprovado.apply {
                setTextColor(Color.parseColor("#FFFFFFFF"))
                setBackgroundResource(R.drawable.button_left_green_quality_control) //Verde
            }
            replaceFragment(ApprovedQualityFragment(mListAprovados))
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
                    mBinding.buttonAprovado.apply {
                        setTextColor(Color.parseColor("#FFFFFFFF"))
                        setBackgroundResource(R.drawable.button_left_green_quality_control) //Verde
                    }
                    replaceFragment(ApprovedQualityFragment(mListAprovados))
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
        }, 100)

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

    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            readingAndress(scanData.toString().trim())
            Log.e("-->", "onNewIntent --> $scanData")
            UIUtil.hideKeyboard(this)
        }
    }

    private fun readingAndress(codBarras: String) {
        clearEdit(mBinding.editQuality)
        mViewModel.getTask1(codBarrasEnd = codBarras)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}