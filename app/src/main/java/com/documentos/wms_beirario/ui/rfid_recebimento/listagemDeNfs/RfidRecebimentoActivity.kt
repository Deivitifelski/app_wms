package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNfs

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidRecebimentoBinding
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNfs.adapter.ListagemNfAdapterRfid
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.extensionSetOnEnterExtensionCodBarrasString
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.registerDataWedgeReceiver

class RfidRecebimentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidRecebimentoBinding
    private lateinit var adapterNf: ListagemNfAdapterRfid
    private lateinit var viewModel: RecebimentoRfidViewModel
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var listNfsSelecionadas = mutableListOf<ResponseGetRecebimentoNfsPendentes>()
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var isPullTraffic = false
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                isPullTraffic = data!!.getBooleanExtra("PULL_TRAFFIC", false)
                if (isPullTraffic) {
                    adapterNf.clear()
                    buscaNfsPendentes()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidRecebimentoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setToolbar()
        setupAdapter()
        setupSharedPreferences()
        cliqueButtonAvancar()
        setupViewModel()
        observerViewModel()
        setupReceiver()
        setupEditTextInput()
        buscaNfsPendentes()
    }


    private fun setupEditTextInput() {
        binding.editNf.extensionSetOnEnterExtensionCodBarrasString { digited ->
            adapterNf.containsInList(context = this, scan = digited)
            binding.editNf.setText("")
            binding.editNf.clearFocus()
        }
    }

    private fun setupReceiver() {
        broadcastReceiver = registerDataWedgeReceiver { scan ->
            adapterNf.containsInList(context = this, scan = scan)
        }
    }

    private fun setToolbar() {
        binding.toolbarRecebimentoRfid.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                finish()
            }
        }
    }


    private fun setupSharedPreferences() {
        sharedPreferences = CustomSharedPreferences(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            RecebimentoRfidViewModel.RecebimentoRfidViewModelFactory(
                RecebimentoRfidRepository()
            )
        )[RecebimentoRfidViewModel::class.java]
    }



    private fun buscaNfsPendentes() {
        val idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        val token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        if (token != null) {
            viewModel.getNfsPendentes(idArmazem = idArmazem, token = token)
        } else alertDefaulSimplesError(message = "Erro ao enviar token do usuário")
    }

    private fun setupAdapter() {
        adapterNf = ListagemNfAdapterRfid { nfs ->
            adicionaIdNfSelecionada(nfs = nfs)
        }
        binding.rvNfRecebimentoRfid.apply {
            adapter = adapterNf
            layoutManager = LinearLayoutManager(this@RfidRecebimentoActivity)
        }
    }

    private fun adicionaIdNfSelecionada(nfs: List<ResponseGetRecebimentoNfsPendentes>) {
        listNfsSelecionadas.clear()
        listNfsSelecionadas.addAll(nfs)
        binding.buttonAvancar.isEnabled = listNfsSelecionadas.isNotEmpty()
        binding.textSizeSelectNf.text = "Total selecionadas: ${listNfsSelecionadas.size}"
    }

    private fun setDataRecyclerView(notasFiscais: List<ResponseGetRecebimentoNfsPendentes>) {
        adapterNf.updateLista(lista = notasFiscais)
    }


    private fun observerViewModel() {
        viewModel.apply {
            retornoComSucessoNfsPendentes()
            retornoSemNfsReceber()
            errorReceberNfsPendentes()
            progressBarVisible()
        }
    }

    private fun RecebimentoRfidViewModel.progressBarVisible() {
        progress.observe(this@RfidRecebimentoActivity) { progress ->
            binding.progressBar.isVisible = progress
        }
    }

    private fun RecebimentoRfidViewModel.retornoSemNfsReceber() {
        sucessRetornaNfsPendentesEmply.observe(this@RfidRecebimentoActivity) { emply ->
            binding.linearNfsEmply.isVisible = emply
        }
    }

    private fun RecebimentoRfidViewModel.retornoComSucessoNfsPendentes() {
        sucessRetornaNfsPendentes.observe(this@RfidRecebimentoActivity) { nfs ->
            setDataRecyclerView(nfs)
        }
    }

    private fun RecebimentoRfidViewModel.errorReceberNfsPendentes() {
        errorDb.observe(this@RfidRecebimentoActivity) { error ->
            alertDefaulSimplesError(message = error)
        }
    }


    private fun cliqueButtonAvancar() {
        binding.buttonAvancar.setOnClickListener {
            Log.e("Tela Nfs", "Nfs: ${ArrayList(listNfsSelecionadas)}")
            val intent = Intent(this, RfidLeituraEpcActivity::class.java)
            intent.putExtra("LISTA_ID_NF", ArrayList(listNfsSelecionadas))
            intent.putExtra("RFID_CONNECTES", isPullTraffic)
            Log.e("TELA NF ->", "ENVIADO -> : $isPullTraffic")
            resultLauncher.launch(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}