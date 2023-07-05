package com.documentos.wms_beirario.ui.qualityControl.activity

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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

class QualityControlActivity : AppCompatActivity(), Observer,
    ApprovedQualityFragment.InterfacePending, RejectedQualityFragment.InterfacePending {


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
    private var mListNaoAprovados = mutableListOf<Rejeitado>()
    private var mValidaRequest = "ALL"
    private var mTrinInit: String? = null
    private var mAprovado: Int = 0
    private var mRejeitado: Int = 0
    private var mShow: String = "ALL"
    private lateinit var mResponseList: ResponseControlQuality1
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                clickButtonLimpar()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityQualityControlctivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initConst()
        setToolbar()
        setupEdit()
        setObserver()
        clickButtons()
        initDataWedge()
        setupDataWedge()
        VALIDA_BUTTON_REQUEST = 0
    }


    private fun setToolbar() {
        mBinding.toolbarQuality.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initConst() {
        mBinding.editLayout.requestFocus()
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
                mViewModel.getTask1(
                    codBarrasEnd = mBinding.editQuality.text.toString().trim()
                )
            } else {
                vibrateExtension(500)
                mToast.toastCustomSucess(this, getString(R.string.edit_emply))
            }
        }
    }

    private fun clickButtons() {
        mBinding.buttonNaoApontados.setOnClickListener {
            setButtonTop(buttonLeft = false)
            replaceFragment(NotApontedQualityFragment(mListNaoApontados))
        }
        mBinding.buttonApontados.setOnClickListener {
            setButtonTop(buttonLeft = true)
            replaceFragment(ApontedQualityFragment(mListApontados))
        }
        //BUTTON REJEITADO -->
        mBinding.buttonRejeitado.setOnClickListener {
            mValidaRequest = "REJEITADO"
            selectedButton(aprovade = false)
            replaceFragment(RejectedQualityFragment(mListNaoAprovados))
        }

        //BUTON APROVADO -->
        mBinding.buttonAprovado.setOnClickListener {
            mValidaRequest = "APROVADO"
            selectedButton(aprovade = true)
            replaceFragment(ApprovedQualityFragment(mListAprovados))
        }
        //Button Next -->
        mBinding.buttonNext.setOnClickListener {
            val intent = Intent(this, QualityControlActivity2::class.java)
            intent.putExtra("REJEITADO", mRejeitado)
            intent.putExtra("APROVADO", mAprovado)
            intent.putExtra("ID_TAREFA", ID_TAREFA_CONTROL_QUALITY)
            intent.putExtra("LIST", mResponseList)
            mResponseBack.launch(intent)
            extensionSendActivityanimation()
        }
    }

    private fun selectedButton(aprovade: Boolean) {
        if (aprovade) {
            mBinding.buttonRejeitado.isActivated = false
            mBinding.buttonAprovado.isActivated = true
            mBinding.buttonAprovado.setTextColor(Color.parseColor("#FFFFFFFF"))
            mBinding.buttonRejeitado.setTextColor(Color.parseColor("#80000000"))
        } else {
            mBinding.buttonAprovado.setTextColor(Color.parseColor("#80000000"))
            mBinding.buttonRejeitado.setTextColor(Color.parseColor("#FFFFFFFF"))
            mBinding.buttonRejeitado.isActivated = true
            mBinding.buttonAprovado.isActivated = false
        }
    }

    //SET BUTTON TOP SELECIONADO -->
    private fun setButtonTop(buttonLeft: Boolean? = null) {
        if (buttonLeft == null) {
            mBinding.buttonNaoApontados.textSize = 12F
            mBinding.buttonApontados.textSize = 12F
            mBinding.buttonNaoApontados.setTextColor(Color.parseColor("#80000000"))
            mBinding.buttonApontados.setTextColor(Color.parseColor("#80000000"))
        } else {
            if (!buttonLeft) {
                mBinding.buttonNaoApontados.textSize = 13F
                mBinding.buttonApontados.textSize = 12F
                mBinding.buttonNaoApontados.setTextColor(Color.parseColor("#FF000000"))
                mBinding.buttonApontados.setTextColor(Color.parseColor("#80000000"))
            } else {
                mBinding.buttonNaoApontados.textSize = 12F
                mBinding.buttonApontados.textSize = 13F
                mBinding.buttonNaoApontados.setTextColor(Color.parseColor("#80000000"))
                mBinding.buttonApontados.setTextColor(Color.parseColor("#FF000000"))
            }
        }
    }


    //Respostas viewModel -->
    private fun setObserver() {
        mViewModel.apply {
            //Progress -->
            mProgressShow.observe(this@QualityControlActivity) { progress ->
                if (progress) mBinding.progressRec.visibility = View.VISIBLE
                else mBinding.progressRec.visibility = View.GONE
            }
            //Response sucesso 1 -->
            mSucessShow.observe(this@QualityControlActivity) { list ->
                clearEdit(mBinding.editQuality)
                if (list != null) {
                    mResponseList = list
                    setButtonNext(list)
                    setButtonLimpar()
                    setListas(list)
                    mBinding.editLayout.hint = "Leia um EAN"
                    setCout(list)
                    setVisibilityButtons(visibility = true)
                    when (mShow) {
                        "ALL" -> {
                            mValidaRequest = "APROVADO"
                            selectedButton(aprovade = true)
                            replaceFragment(ApprovedQualityFragment(mListAprovados))
                        }
                        "APROVADOS" -> {
                            selectedButton(aprovade = true)
                            replaceFragment(ApprovedQualityFragment(mListAprovados))
                        }
                        "REJEITADOS" -> {
                            selectedButton(aprovade = false)
                            replaceFragment(RejectedQualityFragment(mListNaoAprovados))
                        }
                    }
                    Log.e("------------------------->", "$mShow")
                }
            }

            //APROVADOS -->
            mSucessAprovadoShow.observe(this@QualityControlActivity) {
                mShow = "APROVADOS"
                mSonsMp3.somSucess(this@QualityControlActivity)
                mViewModel.getTask1(codBarrasEnd = mTrinInit!!)
            }
            //REJEITADO -->
            mSucessReprovadodoShow.observe(this@QualityControlActivity) {
                mShow = "REJEITADOS"
                mSonsMp3.somSucess(this@QualityControlActivity)
                mViewModel.getTask1(codBarrasEnd = mTrinInit!!)
            }
            //Erro Banco -->
            mErrorHttpShow.observe(this@QualityControlActivity) { error ->
                clearEdit(mBinding.editQuality)
                mAlert.alertMessageErrorSimples(this@QualityControlActivity, error, 5000)
            }
            //Error Geral -->
            mErrorAllShow.observe(this@QualityControlActivity) { error ->
                clearEdit(mBinding.editQuality)
                mAlert.alertMessageErrorSimples(this@QualityControlActivity, error, 5000)
            }
        }
    }

    private fun setButtonNext(list: ResponseControlQuality1) {
        if (list.naoApontados.isEmpty()) {
            mToast.toastDefault(this, "Todos os itens Apontados!")
        }
        mBinding.buttonNext.isEnabled = list.naoApontados.isEmpty()
    }

    //Seta visibildade dos buttons inferiores -->
    private fun setVisibilityButtons(visibility: Boolean) {
        if (visibility) {
            mBinding.apply {
                buttonApontados.visibility = View.VISIBLE
                buttonRejeitado.visibility = View.VISIBLE
                buttonAprovado.visibility = View.VISIBLE
                buttonNaoApontados.visibility = View.VISIBLE
            }
        } else {
            mBinding.apply {
                buttonApontados.visibility = View.INVISIBLE
                buttonRejeitado.visibility = View.INVISIBLE
                buttonAprovado.visibility = View.INVISIBLE
                buttonNaoApontados.visibility = View.INVISIBLE
            }
        }
    }

    //BUTTON LIMPAR -->
    private fun setButtonLimpar() {
        mBinding.frameRv.visibility = View.VISIBLE
        mBinding.buttonLimpar.isEnabled = true
        mBinding.buttonLimpar.setOnClickListener {
            clickButtonLimpar()
        }
    }

    private fun clickButtonLimpar() {
        setButtonTop(buttonLeft = null)
        setVisibilityButtons(visibility = false)
        mBinding.frameRv.visibility = View.INVISIBLE
        ID_TAREFA_CONTROL_QUALITY = ""
        mValidaRequest = "ALL"
        mShow = "ALL"
        mBinding.editLayout.hint = "Leia um TRIN"
        mBinding.buttonLimpar.isEnabled = false
        mBinding.buttonNext.isEnabled = false
        mListApontados.clear()
        mListNaoApontados.clear()
        mListAprovados.clear()
        mListNaoAprovados.clear()
        mBinding.apply {
            buttonApontados.text = "Apontados"
            buttonNaoApontados.text = "Não apontados"
            buttonAprovado.text = "Aprovados"
            buttonRejeitado.text = "Rejeitados"
        }
    }

    //Cria as listas -->
    private fun setListas(list: ResponseControlQuality1) {
        mAprovado = list.aprovados.size
        mRejeitado = list.rejeitados.size
        ID_TAREFA_CONTROL_QUALITY = list.idTarefa
        mListApontados.clear()
        mListNaoApontados.clear()
        mListAprovados.clear()
        mListNaoAprovados.clear()
        mListApontados.addAll(list.apontados.sortedBy { it.sequencial })
        mListNaoApontados.addAll(list.naoApontados.sortedBy { it.sequencial })
        mListAprovados.addAll(list.aprovados.sortedBy { it.sequencial })
        mListNaoAprovados.addAll(list.rejeitados.sortedBy { it.sequencial })
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
        }, 25)
    }

    //Seta a quantidade a ser mostrada nos buttons -->
    private fun setCout(list: ResponseControlQuality1) {
        val apontados = list.apontados.size
        val naoApontados = list.naoApontados.size
        val aprovados = list.aprovados.size
        val rejeitados = list.rejeitados.size
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
            Log.e("-->", "onNewIntent --> $scanData")
            UIUtil.hideKeyboard(this)
            when (mValidaRequest) {
                "ALL" -> {
                    mTrinInit = scanData.toString().trim()
                    readingAndress(scanData.toString().trim())
                }
                "REJEITADO" -> {
                    reandingRejeitado(scanData.toString().trim())
                }
                "APROVADO" -> {
                    reandingAprovado(scanData.toString().trim())
                }
            }
        }
    }

    //CALL REJEITADO -->
    private fun reandingRejeitado(codBarras: String) {
        mViewModel.setRejeitado(
            BodySetAprovadoQuality(
                codigoBarrasEan = codBarras,
                idTarefa = ID_TAREFA_CONTROL_QUALITY
            )
        )
    }

    //CALL Aprovado -->
    private fun reandingAprovado(codBarras: String) {
        mViewModel.setAprovado(
            BodySetAprovadoQuality(
                codigoBarrasEan = codBarras,
                idTarefa = ID_TAREFA_CONTROL_QUALITY
            )
        )
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

    companion object {
        var ID_TAREFA_CONTROL_QUALITY = ""
        var VALIDA_BUTTON_REQUEST = 0
    }


    /**Swipe fragment aprovados ao setar para rejeitados -->*/
    override fun setPendingApproved(set: Boolean) {
        if (set) {
            mSonsMp3.somSucessReading(this)
            mShow = "APROVADOS"
            mViewModel.getTask1(codBarrasEnd = mTrinInit!!)
        }
    }

    /**Swipe fragment rejeitados ao setar para pentendes -->*/
    override fun setPendingReject(set: Boolean) {
        if (set) {
            mSonsMp3.somSucessReading(this)
            mShow = "REJEITADOS"
            mViewModel.getTask1(codBarrasEnd = mTrinInit!!)
        }
    }
}

