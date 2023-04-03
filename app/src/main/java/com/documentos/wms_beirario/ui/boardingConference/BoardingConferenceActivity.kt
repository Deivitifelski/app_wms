package com.documentos.wms_beirario.ui.boardingConference

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityBoardingConferenceBinding
import com.documentos.wms_beirario.model.conferenceBoarding.BodyChaveBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding
import com.documentos.wms_beirario.model.conferenceBoarding.ResponseConferenceBoarding
import com.documentos.wms_beirario.repository.conferenceBoarding.ConferenceBoardingRepository
import com.documentos.wms_beirario.ui.boardingConference.fragments.ApointedBoardingFragment
import com.documentos.wms_beirario.ui.boardingConference.fragments.NotApointedBoardingFragment
import com.documentos.wms_beirario.ui.boardingConference.viewModel.ConferenceBoardingViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.clearEdit
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class BoardingConferenceActivity : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityBoardingConferenceBinding
    private lateinit var mViewModel: ConferenceBoardingViewModel
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mValidaCall = false
    private lateinit var listAproved: MutableList<DataResponseBoarding>
    private lateinit var listNotAproved: MutableList<DataResponseBoarding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBoardingConferenceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initConst()
        initDataWedge()
        setupDataWedge()
        setupEdit()
        setObserver()
        clickBUtton()
    }


    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun setupEdit() {
        mBinding.editConfEmbarque.extensionSetOnEnterExtensionCodBarras {
            if (mBinding.editConfEmbarque.text.toString().isNotEmpty()) {
//                mViewModel.getTask1(codBarrasEnd = mBinding.editConfEmbarque.text.toString().trim())
            } else {
                vibrateExtension(500)
                mToast.toastCustomSucess(this, getString(R.string.edit_emply))
            }
        }
    }

    private fun initConst() {
        listAproved = mutableListOf()
        listNotAproved = mutableListOf()
        mViewModel = ViewModelProvider(
            this,
            ConferenceBoardingViewModel.ConferenceBoardingFactory(ConferenceBoardingRepository())
        )[ConferenceBoardingViewModel::class.java]
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
    }

    //Replace dos fragmentos com s recycler views -->
    private fun replaceFragment(fragment: Fragment) {
        Handler(Looper.myLooper()!!).postDelayed({
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_rv, fragment)
            fragmentTransaction.commit()
        }, 100)
    }

    private fun clickBUtton() {
        mBinding.buttonReject.setOnClickListener {
            mBinding.buttonReject.textSize = 14F
            mBinding.buttonAproved.textSize = 10F
            replaceFragment(NotApointedBoardingFragment(listNotAproved))
        }

        mBinding.buttonAproved.setOnClickListener {
            mBinding.buttonReject.textSize = 10F
            mBinding.buttonAproved.textSize = 14F
            replaceFragment(ApointedBoardingFragment(listAproved))
        }

        mBinding.buttonLimpar.setOnClickListener {
            mValidaCall = false
        }
    }

    private fun setObserver() {
        mViewModel.apply {
            //------------------RESPOSTAS AO BIPAR NF---------------------------------------------->
            mSucessShow.observe(this@BoardingConferenceActivity) { listTask ->
                clearEdit(mBinding.editConfEmbarque)
                if (listTask.isNotEmpty()) {
                    mValidaCall = true
                    mSonsMp3.somSucess(this@BoardingConferenceActivity)
                    countItens(listTask)
                    mBinding.buttonReject.textSize = 10F
                    mBinding.buttonAproved.textSize = 14F
                    Log.e(TAG, "$listTask ")
                }
            }
            mErrorHttpShow.observe(this@BoardingConferenceActivity) { error ->
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            mErrorAllShow.observe(this@BoardingConferenceActivity) { error ->
                mAlert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //---------------------------------------------------------------->
        }
    }

    private fun countItens(list: ResponseConferenceBoarding) {
        var aprointed = 0
        var notAproited = 0
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
            replaceFragment(NotApointedBoardingFragment(listNotAproved))
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
                if (!mValidaCall) {
                    mViewModel.pushNfe(BodyChaveBoarding(codChave = qrCode!!))
                } else {
                    //chamada conferir itens -->

                }
            }
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