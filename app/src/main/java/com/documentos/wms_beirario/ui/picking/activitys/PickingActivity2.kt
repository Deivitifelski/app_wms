package com.documentos.wms_beirario.ui.picking.activitys

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarras
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.google.android.material.chip.Chip
import java.util.Observable
import java.util.Observer


class PickingActivity2 : AppCompatActivity(), Observer {

    private lateinit var binding: ActivityPicking2Binding
    private lateinit var adapterData: AdapterApontadosPicking
    private var idArea: Int = 0
    private var mNameArea: String = ""
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mAlert: CustomAlertDialogCustom
    private lateinit var mediaSonsMp3: CustomMediaSonsMp3
    private lateinit var mViewModel: PickingViewModel2
    private val dwInterface = DWInterface()
    private val receiver = DWReceiver()
    private var initialized = false
    private lateinit var token: String
    private var listaApontados = mutableListOf<PickingResponseTest2>()
    private var listaNaoApontados = mutableListOf<PickingResponseTest2>()
    private var idArmazem: Int = 0
    private lateinit var sharedPreferences: CustomSharedPreferences


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
        cliqueClip()

    }


    private fun getIntentExtas() {
        try {
            if (intent.extras != null)
                idArea = intent.getIntExtra(ID_AREA, 0)
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
        initRecyclerView()
        getVolApontados()
        getVolNaoApontados()
        validadButton()
    }

    private fun initRecyclerView() {
        adapterData = AdapterApontadosPicking(context = this)
        binding.rvPicking.apply {
            layoutManager = LinearLayoutManager(this@PickingActivity2)
            adapter = adapterData
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
        mViewModel.getVolApontados(idArea, idArmazem = idArmazem, token = token)
    }


    private fun getVolNaoApontados() {
        mViewModel.getVolNaoApontados(idArea, idArmazem = idArmazem, token = token)
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
            idArea = idArea,
            pickingRepository = PickingRequest1(scan),
            idArmazem, token
        )
        clearEdit()
    }

    private fun initObserver() {
        /**Retorna itens apontados-->*/
        mViewModel.sucessVolumesApontadosShow.observe(this) { response ->
            if (response.isNotEmpty()) {
                binding.chipApontados.text = "Apontados: ${response.size}"
                val listString = mutableListOf<String>()
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
                        listaApontados.add(
                            PickingResponseTest2(
                                pedido = a[0].pedido ?: "-",
                                enderecoVisualOrigem = a[0].enderecoVisualOrigem,
                                list = listObjList
                            )
                        )
                    }
                }
            } else {
                binding.chipApontados.text = "Apontados: 0"
            }
        }

        /**Retorna itens não apontados-->*/
        mViewModel.sucessVolumesNaoApontadosShow.observe(this) { response ->
            if (response.isNotEmpty()) {
                binding.chipPendentes.text = "Pendentes: ${response.size}"
                val listString = mutableListOf<String>()
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
                            count += 1
                            listObjList.add(
                                PickingResponseTestList2(
                                    numeroSerie = it.numeroSerie
                                )
                            )
                        }
                        listaNaoApontados.add(
                            PickingResponseTest2(
                                pedido = a[0].pedido ?: "-",
                                enderecoVisualOrigem = a[0].enderecoVisualOrigem,
                                list = listObjList
                            )
                        )
                    }
                    adapterData.update(listaNaoApontados)
                }
            } else {
                binding.chipPendentes.text = "Pendentes: 0"
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
            getVolApontados()
            getVolNaoApontados()
            initRecyclerView()
            mediaSonsMp3.somSucess(this)
            mToast.toastCustomSucess(this, "Inserido!")
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


    private fun cliqueClip() {
        binding.chipPendentes.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                when (chip.id) {
                    R.id.chip_pendentes -> {
                        adapterData.update(listaNaoApontados)
                    }

                    R.id.chip_apontados -> {
                        adapterData.update(listaApontados)
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
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
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.hasExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)) {
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