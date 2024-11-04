package com.documentos.wms_beirario.ui.boardingConference

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
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
import com.documentos.wms_beirario.utils.extensions.clearEdit
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.returnNameVersionDb
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*


class BoardingConferenceActivity : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityBoardingConferenceBinding
    private lateinit var viewModel: ConferenceBoardingViewModel
    private lateinit var adapterYes: AdapterConferenceBoardingAdapter
    private lateinit var adapterNot: AdapterNotConferenceBoardingAdapter
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var mSonsMp3: CustomMediaSonsMp3
    private lateinit var alert: CustomAlertDialogCustom
    private lateinit var mToast: CustomSnackBarCustom
    private var mValidaCall = false
    private var contaisNumSerie = false
    private lateinit var listAproved: MutableList<DataResponseBoarding>
    private lateinit var listPending: MutableList<DataResponseBoarding>
    private var mValidaSet = "P"
    private var chaveCurrent = ""
    private var objCurrent: DataResponseBoarding? = null
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var token: String
    private var idArmazem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingConferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        setToolbar()
        initDataWedge()
        setupDataWedge()
        setObserver()
        clickBUtton()
        swipeRv()

    }

    private fun initDataWedge() {
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
    }

    private fun initConst() {
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        hideKeyExtensionActivity(binding.editConfEmbarque)
        binding.apply {
            buttonFinalizar.isEnabled = false
            buttonLimpar.isEnabled = false
        }
        listAproved = mutableListOf()
        listPending = mutableListOf()
        viewModel = ViewModelProvider(
            this,
            ConferenceBoardingViewModel.ConferenceBoardingFactory(ConferenceBoardingRepository())
        )[ConferenceBoardingViewModel::class.java]
        mSonsMp3 = CustomMediaSonsMp3()
        alert = CustomAlertDialogCustom()
        mToast = CustomSnackBarCustom()
        adapterYes = AdapterConferenceBoardingAdapter()
        adapterNot = AdapterNotConferenceBoardingAdapter()
        binding.apply {
            rvApointedBoarding.apply {
                layoutManager = LinearLayoutManager(this@BoardingConferenceActivity)
                adapter = adapterYes
            }
            rvNotApointedBoarding.apply {
                layoutManager = LinearLayoutManager(this@BoardingConferenceActivity)
                adapter = adapterNot
            }
        }
    }

    private fun setToolbar() {
        binding.toolbarCofEmbarque.apply {
            subtitle = returnNameVersionDb()
            setNavigationOnClickListener {
                onBackPressed()
            }
        }


    }

    private fun swipeRv() {
        //Pendentes -->
        binding.rvNotApointedBoarding.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                //apontados -->
                if (listPending[position].ean.isNullOrEmpty() && listPending[position].numeroSerie.isNullOrEmpty()) {
                    alert.alertMessageErrorSimplesAction(
                        context = this@BoardingConferenceActivity,
                        message = "Ean o numSerie inválidos!",
                        action = {
                            notifyAdapter()
                        }
                    )
                } else {
                    try {
                        viewModel.setApproved(
                            BodySetBoarding(
                                idTarefa = listPending[position].idTarefa ?: "",
                                codBarras = if (listPending[position].numeroSerie == null) listPending[position].ean else listPending[position].numeroSerie!!,
                                sequencial = listPending[position].sequencial
                            ),
                            token,
                            idArmazem
                        )
                    } catch (e: Exception) {
                        toastError(
                            this@BoardingConferenceActivity,
                            "Erro ao setar para aprovado!"
                        )
                    }
                }
            }

            override fun onSwipedRight(position: Int) {}
        })
    }


    //Clique nos buttons ------------------------------------------------------------------------->
    private fun clickBUtton() {
        binding.buttonReject.setOnClickListener {
            mValidaSet = "P"
            binding.apply {
                buttonReject.isActivated = true
                buttonAproved.isActivated = false
                rvNotApointedBoarding.isVisible = true
                rvApointedBoarding.isVisible = false
                buttonReject.setTextColor(Color.parseColor("#FFFFFFFF"))
                buttonAproved.setTextColor(Color.parseColor("#80000000"))
            }
        }

        binding.buttonAproved.setOnClickListener {
            mValidaSet = "A"
            binding.apply {
                buttonReject.isActivated = false
                buttonAproved.isActivated = true
                buttonAproved.setTextColor(Color.parseColor("#FFFFFFFF"))
                buttonReject.setTextColor(Color.parseColor("#80000000"))
                rvNotApointedBoarding.isVisible = false
                rvApointedBoarding.isVisible = true
            }
        }

        binding.buttonLimpar.setOnClickListener {
            finishConfButton()
        }

        binding.buttonFinalizar.setOnClickListener {
            alert.alertMessageAtencaoOptionAction(
                context = this,
                message = "Deseja finalizar conferência de embarque?",
                actionYes = {
                    finishConfButton()
                    mToast.toastCustomSucess(this, "conferência realizada com sucesso!")
                    mSonsMp3.somSucess(this)
                },
                actionNo = {
                    mToast.toastDefault(this, "Operação cancelada!")
                }
            )
        }
    }

    private fun finishConfButton() {
        binding.progressBip.visibility = View.VISIBLE
        clearEdit(binding.editConfEmbarque)
        Handler(Looper.myLooper()!!).postDelayed({
            binding.apply {
                mValidaCall = false
                editLayout.hint = "Leia uma Nf-e:"
                buttonLimpar.isEnabled = false
                buttonFinalizar.isEnabled = false
                linearLayoutCompat22.isVisible = false
                binding.progressBip.visibility = View.GONE
                rvApointedBoarding.visibility = View.INVISIBLE
                rvNotApointedBoarding.visibility = View.INVISIBLE
                requisicao.text = ""
                filial.text = ""
                mValidaSet = "P"
            }
        }, 200)
    }

    private fun setObserver() {
        viewModel.apply {
            //------------------RESPOSTAS AO BIPAR NF---------------------------------------------->
            mSucessShow.observe(this@BoardingConferenceActivity) { listTask ->
                clearEdit(binding.editConfEmbarque)
                if (listTask.isNotEmpty()) {
                    contaisNumSerie = false
                    Log.e(TAG, "CHAVE -> $chaveCurrent")
                    listTask.forEach {
                        it.listNotApointed.forEach { data ->
                            contaisNumSerie = data.numeroSerie != null
//                            Log.e(
//                                TAG,
//                                "EAN PENDENTES -> ean ${data.ean} numSerie ${data.numeroSerie}"
//                            )
                        }
//                        it.listApointed.forEach {
//                            Log.e(TAG, "EAN APONTADOS -> ean ${it.ean} numSerie ${it.numeroSerie}")
//                        }
                    }
                    setText(listTask)
                    setInitBip(listTask)
                } else {
                    vibrateExtension(500)
                    mToast.toastDefault(
                        this@BoardingConferenceActivity,
                        getString(R.string.list_emply)
                    )
                }
            }
            //------------------RESPOSTAS AO SETAR ITEM---------------------------------------------->
            mSucessSetShow.observe(this@BoardingConferenceActivity) { listTask ->
                clearEdit(binding.editConfEmbarque)
                if (listTask.isNotEmpty()) {
                    countItens(list = listTask)
                    notifyAdapter()
                    mSonsMp3.somSucess(this@BoardingConferenceActivity)
                } else {
                    vibrateExtension(500)
                    mToast.toastDefault(
                        this@BoardingConferenceActivity,
                        getString(R.string.list_emply)
                    )
                }
            }
            mErrorHttpShow.observe(this@BoardingConferenceActivity) { error ->
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            mErrorAllShow.observe(this@BoardingConferenceActivity) { error ->
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //----------------------------RESPOSTA APPROVED---------------------------------------->
            mSucessApprovedShow.observe(this@BoardingConferenceActivity) { listApproved ->
                viewModel.pushNfeSet(BodyChaveBoarding(codChave = chaveCurrent), token, idArmazem)

            }
            mErrorAllApprovedShow.observe(this@BoardingConferenceActivity) { errorApproved ->
                notifyAdapter()
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, errorApproved)
            }
            mErrorHttpApprovedShow.observe(this@BoardingConferenceActivity) { error ->
                notifyAdapter()
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //----------------------------RESPOSTA REJECT------------------------------------------>
            mSucessFailedShow.observe(this@BoardingConferenceActivity) { listFailed ->
                viewModel.pushNfeSet(
                    BodyChaveBoarding(codChave = chaveCurrent),
                    token,
                    idArmazem
                )
                Log.e(TAG, "BUSCA TAREFAS SUCESS PENDENTES -> $chaveCurrent")
            }
            mErrorHttpFailedShow.observe(this@BoardingConferenceActivity) { errorReject ->
                notifyAdapter()
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, errorReject)
            }
            mErrorAllFailedShow.observe(this@BoardingConferenceActivity) { error ->
                notifyAdapter()
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
            //PROGRESS -->
            mProgressShow.observe(this@BoardingConferenceActivity) { progress ->
                if (progress) binding.progressBip.visibility = View.VISIBLE
                else binding.progressBip.visibility = View.GONE
            }

            sucessEanOkShow.observe(this@BoardingConferenceActivity) { newEan ->
                if (newEan.isNotEmpty()) {
                    if (mValidaSet == "A") {
                        val obj = adapterNot.searchEan(newEan, listPending)
                        if (obj != null) {
                            viewModel.setApproved(
                                BodySetBoarding(
                                    idTarefa = obj.idTarefa ?: "",
                                    codBarras = newEan,
                                    sequencial = obj.sequencial
                                ),
                                token,
                                idArmazem
                            )
                        } else {
                            conferedContaisListAproved(adapterYes.contaisQrCodeEan(qrCode = newEan))
                        }
                    } else {
                        val obj = adapterYes.searchEan(newEan, listAproved)
                        if (obj != null) {
                            viewModel.setPending(
                                BodySetBoarding(
                                    idTarefa = obj.idTarefa ?: "",
                                    codBarras = newEan,
                                    sequencial = obj.sequencial
                                ),
                                token,
                                idArmazem
                            )
                        } else {
                            conferedIsContaisListPendent(adapterYes.contaisQrCodeEan(qrCode = newEan))
                        }
                    }
                } else {
                    clearEdit(binding.editConfEmbarque)
                    alert.alertMessageErrorSimples(
                        this@BoardingConferenceActivity,
                        "Novo ean inválido"
                    )
                }
            }
            errorShow.observe(this@BoardingConferenceActivity) { error ->
                alert.alertMessageErrorSimples(this@BoardingConferenceActivity, error)
            }
        }
    }

    /**
     * Seta os textos lado edit -->
     */
    @SuppressLint("SetTextI18n")
    private fun setText(listTask: ResponseConferenceBoarding?) {
        val info = listTask?.get(0)
        if (info != null) {
            binding.filial.text = "Filial: ${info.filial}"
            if (info.nfNumero != null) {
                binding.requisicao.text = "Nota fiscal: ${info.nfNumero}/${info.nfSerie}"
            }
            if (info.requisicao != null) {
                binding.requisicao.text = "Requisição ${info.requisicao}"
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapter() {
        adapterYes.notifyDataSetChanged()
        adapterNot.notifyDataSetChanged()
    }

    //Bipagem Nf-e -->
    private fun setInitBip(listTask: ResponseConferenceBoarding) {
        binding.apply {
            linearLayoutCompat22.isVisible = true
            buttonLimpar.isEnabled = true
            editLayout.hint = "Leia um cód.Barras:"
            buttonReject.isSelected = true
            buttonReject.isActivated = true
            buttonAproved.isActivated = false
            buttonReject.setTextColor(Color.parseColor("#FFFFFFFF"))
            buttonAproved.setTextColor(Color.parseColor("#80000000"))

        }
        mSonsMp3.somSucess(this@BoardingConferenceActivity)
        countItens(listTask) // Contagem dos itens nos button
        Log.e(TAG, "$listTask ")
    }

    //Verifica se as duas listas não contem itens ai libera button para finalizar -->
    private fun validaButtonFinish(failed: List<DataResponseBoarding>) {
        binding.buttonFinalizar.isEnabled = failed.isEmpty()
    }

    //Faz contagem dos itens e seta as recyclerviews -->
    @SuppressLint("SetTextI18n")
    private fun countItens(list: ResponseConferenceBoarding) {
        list.forEach {
            Log.e(TAG, "${it.listNotApointed.forEach { it.ean }}")
            Log.e(TAG, "${it.listApointed.forEach { it.ean }}")
        }

        var aprointed = 0
        var notAproited = 0
        listAproved.clear()
        listPending.clear()
        list.forEach { item ->
            item.listApointed.forEach {
                it.pedido = item.pedido
                aprointed += 1
                listAproved.add(it)
            }
            item.listNotApointed.forEach {
                it.pedido = item.pedido
                notAproited += 1
                listPending.add(it)
            }
        }
        binding.buttonAproved.text = "Aprovados - $aprointed"
        binding.buttonReject.text = "Pendente - $notAproited"
        adapterYes.update(listAproved)
        adapterNot.update(listPending)
        validaButtonFinish(listPending)
        //Para caso seja a primeira bipagem, mostrar os rejeitados, e valida chamada para TRUE.
        if (!mValidaCall) {
            binding.apply {
                rvApointedBoarding.visibility = View.INVISIBLE
                rvNotApointedBoarding.visibility = View.VISIBLE
            }
            mValidaCall = true
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
            val scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            Log.e("-->", "onNewIntent --> $scanData")
            UIUtil.hideKeyboard(this)
            try {
                scanData.let { qrCode ->
                    if (!mValidaCall) {
                        chaveCurrent = qrCode!!
                        viewModel.pushNfe(
                            BodyChaveBoarding(codChave = qrCode),
                            token,
                            idArmazem
                        )
                    } else {
                        if (mValidaSet == "A") {
                            setApproved(qrCode!!)
                        } else {
                            setPending(qrCode!!)
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao ler QrCode", Toast.LENGTH_SHORT).show()
            } finally {
                clearEdit(binding.editConfEmbarque)
            }
        }
    }

    //Envio QrCode,faz a busca na lista de objetos verifica se existe e faz a chamada ------------->
    private fun setPending(qrCode: String) {
        clearEdit(binding.editConfEmbarque)
        if (contaisNumSerie) {
            objCurrent = adapterYes.lookForNumSerieObject(qrCode, listAproved)
            if (objCurrent != null) {
                viewModel.setPending(
                    BodySetBoarding(
                        idTarefa = objCurrent?.idTarefa ?: "",
                        codBarras = objCurrent!!.numeroSerie!!,
                        sequencial = objCurrent!!.sequencial
                    ),
                    token,
                    idArmazem
                )
            } else {
                conferedIsContaisListPendent(adapterNot.contaisQrCode(qrCode))
            }
        } else {
            viewModel.getEanOK(codBarras = qrCode)
        }
    }

    private fun conferedIsContaisListPendent(functions: Boolean) {
        if (functions) {
            alert.alertMessageAtencao(
                this,
                message = "Item já inserido como pendente!"
            )
        } else {
            alert.alertMessageAtencao(
                this,
                message = "Leitura inválida."
            )
        }
        clearEdit(binding.editConfEmbarque)
    }

    private fun setApproved(qrCode: String) {
        clearEdit(binding.editConfEmbarque)
        if (contaisNumSerie) {
            objCurrent = adapterNot.lookForObject(qrCode, listPending)
            if (objCurrent != null) {
                viewModel.setApproved(
                    BodySetBoarding(
                        idTarefa = objCurrent?.idTarefa ?: "",
                        codBarras = objCurrent!!.numeroSerie!!,
                        sequencial = objCurrent!!.sequencial
                    ),
                    token,
                    idArmazem
                )
            } else {
                conferedContaisListAproved(adapterYes.contaisQrCode(qrCode))
            }
        } else {
            viewModel.getEanOK(codBarras = qrCode)
        }
    }

    private fun conferedContaisListAproved(function: Boolean) {
        if (function) {
            alert.alertMessageAtencao(
                this,
                message = "Item já inserido como aprovado!"
            )
        } else {
            alert.alertMessageAtencao(
                this,
                message = "Leitura inválida."
            )
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