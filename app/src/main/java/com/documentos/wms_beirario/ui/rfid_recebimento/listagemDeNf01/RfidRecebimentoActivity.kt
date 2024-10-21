package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityRfidRecebimentoBinding
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseGetRecebimentoNfsPendentes
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueApontmentoViewModelCv
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01.adapter.ListagemNfAdapterRfid
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
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

        setupAdapter()
        setupSharedPreferences()
        setDataRecyclerView()
        cliqueButtonAvancar()
        setupViewModel()
        observerViewModel()
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
        toastDefault(message = "Qtd selecionados: ${listNfsSelecionadas.size}")
    }

    private fun setDataRecyclerView() {
        val notasFiscais = listOf(
            ResponseGetRecebimentoNfsPendentes(
                idArmazem = 67,
                idDocumento = "05420030CF66F2B1E0636F00960A836B",
                filial = 10,
                nfNumero = 223796,
                nfSerie = 3,
                nfDataEmissao = "13/09/2023",
                quantidadeNumeroSerie = 2
            ),
            ResponseGetRecebimentoNfsPendentes(
                idArmazem = 67,
                idDocumento = "1EBB4EA1F5637191E0636C0000A5B2F2",
                filial = 23,
                nfNumero = 66491,
                nfSerie = 5,
                nfDataEmissao = "02/08/2024",
                quantidadeNumeroSerie = 3
            ),
            ResponseGetRecebimentoNfsPendentes(
                idArmazem = 67,
                idDocumento = "2046146B8AA1605E0636C0000A0843",
                filial = 23,
                nfNumero = 66533,
                nfSerie = 5,
                nfDataEmissao = "22/08/2024",
                quantidadeNumeroSerie = 2
            )
        )
        adapterNf.updateLista(lista = notasFiscais)
    }


    private fun observerViewModel() {
        viewModel.apply {
            retornoComSucessoNfsPendentes()
            retornoSemNfsReceber()
            errorReceberNfsPendentes()
        }
    }

    private fun RecebimentoRfidViewModel.retornoSemNfsReceber() {
        sucessRetornaNfsPendentesEmply.observe(this@RfidRecebimentoActivity) {
            toastDefault(message = it)
        }
    }

    private fun RecebimentoRfidViewModel.retornoComSucessoNfsPendentes() {
        sucessRetornaNfsPendentes.observe(this@RfidRecebimentoActivity) {

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