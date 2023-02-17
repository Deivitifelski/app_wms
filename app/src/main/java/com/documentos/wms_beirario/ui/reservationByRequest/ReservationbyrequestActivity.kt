package com.documentos.wms_beirario.ui.reservationByRequest

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityReservationbyrequestBinding
import com.documentos.wms_beirario.model.reservationByRequest.BodyAddReservation1
import com.documentos.wms_beirario.model.reservationByRequest.BodyAddVolReservationByRequest
import com.documentos.wms_beirario.model.reservationByRequest.ResponseReservationPed1
import com.documentos.wms_beirario.repository.reservationByRequest.ReservationByRequestRepository
import com.documentos.wms_beirario.ui.reservationByRequest.adapter.AdapterReservation
import com.documentos.wms_beirario.ui.reservationByRequest.viewModel.ReservationByRequestViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*

class ReservationbyrequestActivity : AppCompatActivity(), Observer {

    private lateinit var mBinding: ActivityReservationbyrequestBinding
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mViewModel: ReservationByRequestViewModel
    private var mValida: Boolean = false
    private var mCodPedido: String? = null
    private lateinit var mAdapter: AdapterReservation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityReservationbyrequestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initConst()
        initDataWedge()
        setupDataWedge()
        setObserver()
        setupEdit()
    }

    private fun initConst() {
        mAdapter = AdapterReservation()
        mSonsMp3 = CustomMediaSonsMp3()
        mAlert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        mViewModel = ViewModelProvider(
            this, ReservationByRequestViewModel.ReservationViewModelFactory(
                ReservationByRequestRepository()
            )
        )[ReservationByRequestViewModel::class.java]
        mBinding.rvVolumes.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ReservationbyrequestActivity)
        }
    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }


    private fun setupDataWedge() {
        ObservableObject.instance.addObserver(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
    }

    //Configurar edit -->
    private fun setupEdit() {
        mBinding.editPed.extensionSetOnEnterExtensionCodBarrasString { codBaras ->
            try {
                if (codBaras.isNotEmpty()) {
                    mViewModel.addPedido(BodyAddReservation1(codPedido = codBaras))
                } else {
                    vibrateExtension(500)
                    mToast.toastCustomSucess(this, getString(R.string.validat_input))
                }
            } catch (e: Exception) {
                vibrateExtension(500)
                mToast.toastCustomError(this, getString(R.string.error_default))
            } finally {
                UIUtil.hideKeyboard(this)
                clearEdit(mBinding.editPed)
            }
        }
    }

    //Respostas viewModel -->
    private fun setObserver() {
        mViewModel.apply {
            //Progress -->
            mProgressShow.observe(this@ReservationbyrequestActivity) { progress ->
                if (progress) mBinding.progressReserPed.visibility = View.VISIBLE
                else mBinding.progressReserPed.visibility = View.GONE
            }
            //Response sucesso 1 -->
            mSucessShow.observe(this@ReservationbyrequestActivity) { list ->
                clearEdit(mBinding.editPed)
                if (list != null) {
                    mBinding.cardInfPedido.visibility = View.VISIBLE
                    setInputs(list)
                } else {
                    mBinding.cardInfPedido.visibility = View.GONE
                }
            }
            //Response Sucesso ao adicionar Volumes -->
            mSucessAddVolShow.observe(this@ReservationbyrequestActivity) { listVol ->
                if (listVol.isNotEmpty()) {
                    mAdapter.submitList(listVol)
                }
            }
            //Erro Banco -->
            mErrorHttpShow.observe(this@ReservationbyrequestActivity) { error ->
                clearEdit(mBinding.editPed)
                mAlert.alertMessageErrorSimplesAction(
                    this@ReservationbyrequestActivity,
                    error,
                    action = { clearEdit(mBinding.editPed) })
            }
            //Error Geral -->
            mErrorAllShow.observe(this@ReservationbyrequestActivity) { error ->
                clearEdit(mBinding.editPed)
                mAlert.alertMessageErrorSimples(this@ReservationbyrequestActivity, error)
            }
        }
    }

    private fun setInputs(item: ResponseReservationPed1) {
        mCodPedido = item.numeroPedido.toString()
        mBinding.produtoApi.text = item.nomeProduto ?: ""
        mBinding.dateApi.text = AppExtensions.formatData(item.dataInclusao.toString()) ?: ""
        mBinding.qntApi.text = item.quantidade.toString() ?: ""
        mBinding.situacaoApi.text = item.situacaoReserva ?: ""
        mBinding.numPedido.text = item.numeroPedido.toString() ?: ""
    }


    override fun update(o: Observable?, arg: Any?) {}
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            if (scanData != null) {
                if (!mValida) {
                    reandingCodBarrasAddPedido(scanData.extensionTrim())
                } else {
                    reandingCodBarrasAddVol(scanData.extensionTrim())
                }
                clearEdit(mBinding.editPed)
                Log.e("ZEBRA ->", "$scanData")
            }
        }
    }

    private fun reandingCodBarrasAddVol(codBarras: String) {
        if (mCodPedido != null) {
            mViewModel.addVol(
                BodyAddVolReservationByRequest(
                    codPedido = mCodPedido!!,
                    numSerie = codBarras
                )
            )
        }
    }

    private fun reandingCodBarrasAddPedido(codBaras: String) {
        clearEdit(mBinding.editPed)
        mViewModel.addPedido(BodyAddReservation1(codPedido = codBaras))

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