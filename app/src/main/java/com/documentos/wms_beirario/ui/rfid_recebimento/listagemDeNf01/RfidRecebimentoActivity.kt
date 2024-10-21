package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidRecebimentoBinding
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01.adapter.ListagemNfAdapterRfid
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastDefault

class RfidRecebimentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidRecebimentoBinding
    private lateinit var adapterNf: ListagemNfAdapterRfid
    private lateinit var viewModel: RecebimentoRfidViewModel
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var listNfsSelecionadas = mutableListOf<ResponseGetRecebimentoNfsPendentes>()
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

    override fun onResume() {
        super.onResume()
        buscaNfsPendentes()
    }

    private fun buscaNfsPendentes() {
        val idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        viewModel.getNfsPendentes(idArmazem = idArmazem)
    }

    private fun setupAdapter() {
        adapterNf = ListagemNfAdapterRfid { nf ->
            adicionaIdNfSelecionada(nf)
        }
        binding.rvNfRecebimentoRfid.apply {
            adapter = adapterNf
            layoutManager = LinearLayoutManager(this@RfidRecebimentoActivity)
        }
    }

    private fun adicionaIdNfSelecionada(nf: ResponseGetRecebimentoNfsPendentes) {
        if (listNfsSelecionadas.contains(nf)) {
            listNfsSelecionadas.remove(nf)
        } else {
            listNfsSelecionadas.add(nf)
        }
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
        errorDb.observe(this@RfidRecebimentoActivity) {
            toastDefault(message = it)
        }
    }


    private fun cliqueButtonAvancar() {
        binding.buttonAvancar.setOnClickListener {
            val intent = Intent(this, RfidLeituraEpcActivity::class.java)
            intent.putExtra("LISTA_ID_NF", ArrayList(listNfsSelecionadas))
            startActivity(intent)
            Log.e("-->", "ENVIANDO: ${ArrayList(listNfsSelecionadas)}")
        }
    }
}