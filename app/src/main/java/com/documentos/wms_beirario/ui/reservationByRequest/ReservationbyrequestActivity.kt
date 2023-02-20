package com.documentos.wms_beirario.ui.reservationByRequest

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.documentos.wms_beirario.model.reservationByRequest.ResponseRservationByRequest1
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
        clickButton()
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

    //Configurar edit , variavel mValida verifica qual passo operador esta e qual metodo deve chamar-->
    private fun setupEdit() {
        mBinding.editPed.extensionSetOnEnterExtensionCodBarrasString { codBaras ->
            try {
                if (codBaras.isNotEmpty()) {
                    if (!mValida) {
                        mViewModel.addPedido(BodyAddReservation1(codPedido = codBaras))
                    } else {
                        if (mCodPedido != null) {
                            mViewModel.addVol(
                                BodyAddVolReservationByRequest(
                                    codPedido = mCodPedido!!,
                                    numSerie = codBaras
                                )
                            )
                        }
                    }
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

    //CLique button alterar pedido -->
    private fun clickButton() {
        mBinding.buttonChangedRequest.setOnClickListener {
            mBinding.progressReserPed.visibility = View.VISIBLE
            mValida = false
            Handler(Looper.myLooper()!!).postDelayed({
                mBinding.editLayout.hint = "Leia um Pedido"
                mBinding.progressReserPed.visibility = View.INVISIBLE
                mAdapter.submitList(null)
                mBinding.cardInfPedido.visibility = View.GONE
            }, 300)
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
                    mValida = true
                    mBinding.editLayout.hint = "Leia um num.Série"
                    mBinding.cardInfPedido.visibility = View.VISIBLE
                    setInputs(list)
                } else {
                    mBinding.cardInfPedido.visibility = View.GONE
                }
            }
            //Response Sucesso ao adicionar Volumes -->
            mSucessAddVolShow.observe(this@ReservationbyrequestActivity) { listVol ->
                clearEdit(mBinding.editPed)
                if (listVol != null) {
                    mValida = true
                    mBinding.cardInfPedido.visibility = View.VISIBLE
                    setInputs(listVol)
                } else {
                    mBinding.cardInfPedido.visibility = View.GONE
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

    private fun setInputs(item: ResponseRservationByRequest1) {
        try {
            mCodPedido = item.pedido.toString()
            mBinding.clienteApi.text = item.cliente ?: ""
            mBinding.dateApi.text = AppExtensions.formatDataEHoraMov(item.dataInclusao) ?: ""
            mBinding.qntApi.text = item.quantidade.toString() ?: ""
            mBinding.situacaoApi.text = item.situacao ?: ""
            mBinding.numPedido.text = item.pedido.toString() ?: ""
            if (item.volumes.isNotEmpty()) {
                mAdapter.submitList(item.volumes)
            }
        } catch (e: Exception) {
            mToast.toastCustomError(this, getString(R.string.error_default))
        }
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