package com.documentos.wms_beirario.ui.boardingConference

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityBoardingConferenceBinding
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.BodySetBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.ResponseConferenceBoarding
import com.documentos.wms_beirario.repository.conferenceBoarding.ConferenceBoardingRepository
import com.documentos.wms_beirario.ui.boardingConference.adapter.AdapterConferenceBoardingAdapter
import com.documentos.wms_beirario.ui.boardingConference.adapter.AdapterNotConferenceBoardingAdapter
import com.documentos.wms_beirario.ui.boardingConference.viewModel.ConferenceBoardingViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*


class BoardingConferenceActivity : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityBoardingConferenceBinding
    private lateinit var mViewModel: ConferenceBoardingViewModel
    private lateinit var mAdapterYes: AdapterConferenceBoardingAdapter
    private lateinit var mAdapterNot: AdapterNotConferenceBoardingAdapter
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mValidaCall = false
    private lateinit var listAproved: MutableList<DataResponseBoarding>
    private lateinit var listNotAproved: MutableList<DataResponseBoarding>
    private var mValidaSet = "R"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBoardingConferenceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        onBack()
        initConst()
        initDataWedge()
        setupDataWedge()
        setObserver()
        clickBUtton()
        swipeRv()

    }

    private fun onBack() {
        mBinding.toolbarCofEmbarque.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initConst() {
        hideKeyExtensionActivity(mBinding.editConfEmbarque)
        mBinding.apply {
            buttonFinalizar.isEnabled = false
            buttonLimpar.isEnabled = false
        }
        listAproved = mutableListOf()
        listNotAproved = mutableListOf()
        mViewModel = ViewModelProvider(
            this,
            ConferenceBoardingViewModel.ConferenceBoardingFactory(ConferenceBoardingRepository())
        )[ConferenceBoardingViewModel::class.java]
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        mAdapterYes = AdapterConferenceBoardingAdapter()
        mAdapterNot = AdapterNotConferenceBoardingAdapter()
        mBinding.apply {
            rvApointedBoarding.apply {
                layoutManager = LinearLayoutManager(this@BoardingConferenceActivity)
                adapter = mAdapterYes
            }
            rvNotApointedBoarding.apply {
                layoutManager = LinearLayoutManager(this@BoardingConferenceActivity)
                adapter = mAdapterNot
            }
        }
    }

    private fun swipeRv() {
        //Apontados -->
        mBinding.rvApointedBoarding.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                //apontados -->
                mToast.toastDefault(this@BoardingConferenceActivity, "left")
            }

            override fun onSwipedRight(position: Int) {
                //delete -->
                mToast.toastDefault(this@BoardingConferenceActivity, "right")
            }
        })
        //Não apontados -->
        mBinding.rvNotApointedBoarding.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                //apontados -->
                mViewModel.setApproved(
                    BodySetBoarding(
                        idTarefa = listNotAproved[position].idTarefa ?: "",
                        codBarras = listNotAproved[position].numeroSerie
                    )
                )
            }

            override fun onSwipedRight(position: Int) {
                //delete -->
                mViewModel.setFailed(
                    BodySetBoarding(
                        idTarefa = listNotAproved[position].idTarefa ?: "",
                        codBarras = listNotAproved[position].numeroSerie
                    )
                )
            }
        })
    }

    private fun clickBUtton() {
        mBinding.buttonReject.setOnClickListener {
            mValidaSet = "R"
            mBinding.apply {
                rvNotApointedBoarding.isVisible = true
                rvApointedBoarding.isVisible = false
            }
        }

        mBinding.buttonAproved.setOnClickListener {
            mValidaSet = "A"
            mBinding.apply {
                rvNotApointedBoarding.isVisible = false
                rvApointedBoarding.isVisible = true
            }
        }

        mBinding.buttonLimpar.setOnClickListener {
            finishConfButton()
        }

        mBinding.buttonFinalizar.setOnClickListener {
            mAlert.alertMessageSucessAction(
                context = this,
                message = "Deseja finalizar conferência de embarque?",
                action = {
                    finishConfButton()
                }
            )
        }
    }

    private fun finishConfButton() {
        mBinding.progressBip.visibility = View.VISIBLE
        clearEdit(mBinding.editConfEmbarque)
        Handler(Looper.myLooper()!!).postDelayed({
            mBinding.apply {
                mValidaCall = false
                editLayout.hint = "Leia uma Nf-e:"
                buttonLimpar.isEnabled = false
                buttonFinalizar.isEnabled = false
                buttonGroupEmbarque.isVisible = false
                mBinding.progressBip.visibility = View.GONE
                rvApointedBoarding.visibility = View.INVISIBLE
                rvNotApointedBoarding.visibility = View.INVISIBLE
                mValidaSet = "R"
            }
        }, 200)
    }

    private fun setObserver() {
        mViewModel.apply {
            //------------------RESPOSTAS AO BIPAR NF---------------------------------------------->
            mSucessShow.observe(this@BoardingConferenceActivity) { listTask ->
                clearEdit(mBinding.editConfEmbarque)
                if (listTask.isNotEmpty()) {
                    setInitBip(listTask)
                }
            }
            mErrorHttpShow.observe(this@BoardingConferenceActivity) { error ->
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            mErrorAllShow.observe(this@BoardingConferenceActivity) { error ->
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //----------------------------RESPOSTA APPROVED------------------------------------>
            mSucessApprovedShow.observe(this@BoardingConferenceActivity) { listApproved ->
                mSonsMp3.somSucess(this@BoardingConferenceActivity)
                validaButtonFinish(listApproved, listNotAproved)
            }
            mErrorAllApprovedShow.observe(this@BoardingConferenceActivity) { errorApproved ->
                mAdapterYes.notifyDataSetChanged()
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, errorApproved)
            }
            mErrorAllApprovedShow.observe(this@BoardingConferenceActivity) { error ->
                mAdapterYes.notifyDataSetChanged()
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //----------------------------RESPOSTA REJECT------------------------------------>
            mSucessFailedShow.observe(this@BoardingConferenceActivity) { listFailed ->
                mSonsMp3.somSucess(this@BoardingConferenceActivity)
                validaButtonFinish(listAproved, listFailed)

            }
            mErrorHttpFailedShow.observe(this@BoardingConferenceActivity) { errorReject ->
                mAdapterNot.notifyDataSetChanged()
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, errorReject)
            }
            mErrorAllFailedShow.observe(this@BoardingConferenceActivity) { error ->
                mAdapterNot.notifyDataSetChanged()
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //PROGRESS -->
            mProgressShow.observe(this@BoardingConferenceActivity) { progress ->
                if (progress) mBinding.progressBip.visibility = View.VISIBLE
                else mBinding.progressBip.visibility = View.GONE
            }
        }
    }

    //Bipagem Nf-e -->
    private fun setInitBip(listTask: ResponseConferenceBoarding) {
        mBinding.apply {
            buttonGroupEmbarque.isVisible = true
            buttonLimpar.isEnabled = true
            editLayout.hint = "Leia um cód.Barras:"
            buttonReject.isChecked = true
        }
        mSonsMp3.somSucess(this@BoardingConferenceActivity)
        countItens(listTask) // Contagem dos itens nos button
        Log.e(TAG, "$listTask ")
    }

    private fun validateRv() {
        if (!mValidaCall || mValidaSet != "A") {
            mBinding.apply {
                rvApointedBoarding.visibility = View.INVISIBLE
                rvNotApointedBoarding.visibility = View.VISIBLE
            }
            mAdapterNot.submitList(listNotAproved)
            mValidaCall = true
        } else {
            mBinding.apply {
                rvApointedBoarding.visibility = View.VISIBLE
                rvNotApointedBoarding.visibility = View.INVISIBLE
            }
            mAdapterYes.submitList(listAproved)
        }
    }

    //Verifica se as duas listas não contem itens ai libera button para finalizar -->
    private fun validaButtonFinish(
        approved: List<DataResponseBoarding>,
        failed: List<DataResponseBoarding>
    ) {
        if (approved.isEmpty() && failed.isEmpty()) {
            mBinding.buttonFinalizar.isEnabled = true
        }
    }

    //Faz contagem dos itens -->
    private fun countItens(list: ResponseConferenceBoarding) {
        var aprointed = 0
        var notAproited = 0
        listAproved.clear()
        listNotAproved.clear()
        list.forEach { item ->
            item.listApointed.forEach {
                aprointed += 1
                listAproved.add(it)
            }
            item.listNotApointed.forEach {
                notAproited += 1
                listNotAproved.add(it)
            }
            mBinding.buttonAproved.text = "Aprovados - $aprointed"
            mBinding.buttonReject.text = "Reprovados - $notAproited"
            validateRv() //Valida qual rv mostrar
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
            scanData.let { qrCode ->
                clearEdit(mBinding.editConfEmbarque)
                if (!mValidaCall) {
                    mViewModel.pushNfe(BodyChaveBoarding(codChave = qrCode!!))
                } else {
                    if (mValidaSet == "A") {
                        setApproved(qrCode!!)
                    } else {
                        setReject(qrCode!!)
                    }
                }
            }
        }
    }

    //Envio QrCode,faz a busca na lista de objetos verifica se existe e faz a chamada ------------->
    private fun setReject(qrCode: String) {
        val obj = mAdapterNot.lookForObject(qrCode)
        if (obj != null) {
            mViewModel.setFailed(
                BodySetBoarding(
                    idTarefa = obj.idTarefa ?: "",
                    codBarras = obj.numeroSerie
                )
            )
        } else {
            mAlert.alertMessageErrorSimples(this, "Código incorreto para Nf.")
        }
    }

    private fun setApproved(qrCode: String) {
        val obj = mAdapterYes.lookForObject(qrCode)
        if (obj != null) {
            mViewModel.setApproved(
                BodySetBoarding(
                    idTarefa = obj.idTarefa ?: "",
                    codBarras = obj.numeroSerie
                )
            )
        } else {
            mAlert.alertMessageErrorSimples(this, "Código incorreto para Nf.")
        }
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