package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.DWInterface
import com.documentos.wms_beirario.data.DWReceiver
import com.documentos.wms_beirario.data.ObservableObject
import com.documentos.wms_beirario.databinding.ActivityPicking2Binding
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.model.picking.PickingResponseTest2
import com.documentos.wms_beirario.model.picking.PickingResponseTestList2
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.AdapterApontadosPicking
import com.documentos.wms_beirario.ui.picking.adapters.AdapterNaoApontadosPicking
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.somSucess
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import java.util.*


class PickingActivity2 : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityPicking2Binding
    private lateinit var adapterApontados: AdapterApontadosPicking
    private lateinit var adapterNaoApontados: AdapterNaoApontadosPicking
    private var mIdArea: Int = 0
    private var mNameArea: String = ""
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mViewModel: PickingViewModel2
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var token: String
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var emply = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPicking2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getIntentExtas()
        initToolbar()
        initViewModel()
        initConst()
        initObserver()
        lerItem()
        finalizarPicking()
        setupDataWedge()
        visibleApontados()

    }

    private fun visibleApontados() {
        binding.checkShow.setOnCheckedChangeListener { _, check ->
            if (check) {
                binding.rvPickingApontados.visibility = View.VISIBLE
                if (emply) {
                    toastDefault(this, "Nenhum volume apontado")
                    binding.txtAllApontados.visibility = View.GONE
                } else {
                    binding.txtAllApontados.visibility = View.VISIBLE
                }

            } else {
                binding.rvPickingApontados.visibility = View.GONE
                binding.txtAllApontados.visibility = View.GONE
            }
        }
    }

    private fun getIntentExtas() {
        try {
            if (intent.extras != null)
                mIdArea = intent.getIntExtra(ID_AREA, 0)
            mNameArea = intent.getStringExtra("NAME_AREA").toString()
        } catch (e: Exception) {
            mErrorToast(e.toString())
        }
    }

    private fun initConst() {
        sharedPreferences = CustomSharedPreferences(this)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        binding.editPicking2.requestFocus()
        mToast = CustomSnackBarCustom()
        mAlert = CustomAlertDialogCustom()
        mediaSonsMp3 = CustomMediaSonsMp3()
        binding.buttonfinalizarpickin2.isEnabled = false
        binding.progressBarInitPicking2.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (!initialized) {
            dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_GET_VERSION, "")
            initialized = true
        }
        getVolApontados()
        getVolNaoApontados()
        initRecyclerView()
        validadButton()
    }

    private fun initRecyclerView() {
        adapterApontados = AdapterApontadosPicking(context = this)
        adapterNaoApontados = AdapterNaoApontadosPicking(context = this)
        binding.rvPickingApontados.apply {
            layoutManager = LinearLayoutManager(this@PickingActivity2)
            adapter = adapterApontados
        }

        binding.rvPickingNaoBipados.apply {
            layoutManager = LinearLayoutManager(this@PickingActivity2)
            adapter = adapterNaoApontados
        }
    }

    private fun initToolbar() {
        binding.toolbarPicking2.apply {
            setNavigationOnClickListener {
                onBackPressed()
            }
            title = "PICKING | $mNameArea"
            subtitle = getVersionNameToolbar()
        }
    }

    private fun validadButton() {
        mViewModel.getItensPickingFinishValidadButton(idArmazem, token)
    }

    private fun getVolApontados() {
        mViewModel.getVolApontados(mIdArea, idArmazem = idArmazem, token = token)
    }


    private fun getVolNaoApontados() {
        mViewModel.getVolNaoApontados(mIdArea, idArmazem = idArmazem, token = token)
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            PickingViewModel2.Picking2ViewModelFactory(PickingRepository())
        )[PickingViewModel2::class.java]
    }

    private fun lerItem() {
        binding.editPicking2.extensionSetOnEnterExtensionCodBarras {
            if (binding.editPicking2.toString() != "") {
                sendData(binding.editPicking2.text.toString().trim())
                clearEdit()
            } else {
                mErrorToast(getString(R.string.edit_emply))
            }
        }
    }

    private fun clearEdit() {
        binding.editPicking2.setText("")
        binding.editPicking2.text?.clear()
        binding.editPicking2.requestFocus()
    }

    private fun sendData(scan: String) {
        mViewModel.getItensPickingReanding2(
            idArea = mIdArea,
            pickingRepository = PickingRequest1(scan),
            idArmazem, token
        )
        clearEdit()
    }

    private fun initObserver() {
        /**Retorna itens apontados-->*/
        mViewModel.sucessVolumesApontadosShow.observe(this) { response ->
            if (response.isEmpty()) {
                emply = true
                binding.txtInfApontados.visibility = View.VISIBLE
                binding.txtAllApontados.visibility = View.INVISIBLE
            } else {
                emply = false
                val listString = mutableListOf<String>()
                val listObj = mutableListOf<PickingResponseTest2>()

                response.forEach {
                    listString.add(it.pedido)
                }
                val listDistinct = listString.distinct()
                var count = 0
                listDistinct.forEach { pedido ->
                    val listObjList = mutableListOf<PickingResponseTestList2>()
                    val a = response.filter { it.pedido == pedido }
                    if (a.isNotEmpty()) {
                        a.forEach {
                            count += 1
                            listObjList.add(
                                PickingResponseTestList2(
                                    numeroSerie = it.numeroSerie
                                )
                            )
                        }
                        listObj.add(
                            PickingResponseTest2(
                                pedido = a[0].pedido ?: "-",
                                enderecoVisualOrigem = a[0].enderecoVisualOrigem,
                                list = listObjList
                            )
                        )
                    }
                }
                binding.txtAllApontados.text = "Total apontados: ${count}"
                binding.txtInfApontados.visibility = View.GONE
                adapterApontados.update(listObj)
                listObj.clear()
            }
        }

        /**Retorna itens não apontados-->*/
        mViewModel.sucessVolumesNaoApontadosShow.observe(this) { response ->
            if (response.isEmpty()) {
                binding.txtInfNaoApontados.visibility = View.VISIBLE
                binding.txtAllNaoApontados.visibility = View.INVISIBLE
            } else {
                val listString = mutableListOf<String>()
                val listObj = mutableListOf<PickingResponseTest2>()
                var count = 0
                response.forEach {
                    listString.add(it.pedido)
                }
                val listDistinct = listString.distinct()
                listDistinct.forEach { pedido ->
                    val listObjList = mutableListOf<PickingResponseTestList2>()
                    val a = response.filter { it.pedido == pedido }
                    if (a.isNotEmpty()) {
                        a.forEach {
                            Log.e("NUMERO DE SÉRIE", it.numeroSerie)
                            count += 1
                            listObjList.add(
                                PickingResponseTestList2(
                                    numeroSerie = it.numeroSerie
                                )
                            )
                        }
                        listObj.add(
                            PickingResponseTest2(
                                pedido = a[0].pedido ?: "-",
                                enderecoVisualOrigem = a[0].enderecoVisualOrigem,
                                list = listObjList
                            )
                        )
                    }
                }

                binding.txtAllNaoApontados.text = "Total pendentes: $count"
                binding.txtAllNaoApontados.visibility = View.VISIBLE
                binding.txtInfNaoApontados.visibility = View.GONE
                adapterNaoApontados.update(listObj)
                listObj.clear()
            }
        }

        mViewModel.mErrorAllShow.observe(this) { errorAll ->
            mAlert.alertMessageErrorSimples(this, errorAll)
        }
        mViewModel.mErrorPickingShow.observe(this) { errorGetPicking ->
            mAlert.alertMessageErrorSimples(this, errorGetPicking)
        }
        mViewModel.mValidProgressInitShow.observe(this) { progressInit ->
            if (progressInit)
                binding.progressBarInitPicking2.visibility = View.VISIBLE
            else binding.progressBarInitPicking2.visibility = View.GONE
        }
        /**RESPOSTAS DA LEITURA -->*/
        mViewModel.sucessReandingPicking.observe(this) {
            clearEdit()
            initRecyclerView()
            getVolApontados()
            getVolNaoApontados()
            validadButton()
            somSucess(this)
            toastDefault(this, "Inserido")
            vibrateExtension(500)
        }

        mViewModel.mErrorReadingPickingShow.observe(this) { erroReanding ->
            mAlert.alertMessageErrorSimplesAction(this, erroReanding, action = { clearEdit() })
        }

        /**FAZ O GET DA TELA FINAL PARA VER SE CONTEM ITENS PARA HABILITAR O BUTTON-->*/
        mViewModel.mSucessShow.observe(this) { sucessValidaButton ->
            binding.buttonfinalizarpickin2.isEnabled = sucessValidaButton.isNotEmpty()

        }

        mViewModel.mErrorShow.observe(this) { sucessValidaButton ->
            mToast.toastCustomError(this, "Erro ao validar button!\n$sucessValidaButton")
        }
    }


    /**BUTTON FINALIZAR PICKING -->*/
    private fun finalizarPicking() {
        binding.buttonfinalizarpickin2.setOnClickListener {
            val intent = Intent(this, PickingActivityFinish::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun mErrorToast(msg: String) {
        mToast.toastCustomError(this, msg)
        vibrateExtension(500)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        extensionBackActivityanimation(this)
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
            sendData(scanData.toString())
            Log.e("PICKING 2", "Recebido onNewIntent --> ${scanData.toString()}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        const val ID_AREA = "id_area_picking_1"
    }
}