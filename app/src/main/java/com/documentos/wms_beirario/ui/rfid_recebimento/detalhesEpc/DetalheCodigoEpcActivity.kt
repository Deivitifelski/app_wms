package com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityDetalheCodigoEpcBinding
import com.documentos.wms_beirario.model.recebimentoRfid.BodyRecbimentoRfidPostDetalhesEpc
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseDetailsEpc
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation

class DetalheCodigoEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheCodigoEpcBinding
    private lateinit var epcDetails: String
    private var TAG = "Detalhes EPC"
    private lateinit var viewModel: RecebimentoRfidViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheCodigoEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupViewModel()
        searchDetaisEpc()
        setupToolbar()
        observerViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarRecebimentoRfidDetalhes.apply {
            setNavigationOnClickListener { finish() }
            subtitle = "Código TAG: $epcDetails"
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            RecebimentoRfidViewModel.RecebimentoRfidViewModelFactory(
                RecebimentoRfidRepository()
            )
        )[RecebimentoRfidViewModel::class.java]
    }

    private fun getIntentData() {
        try {
            if (intent != null) {
                epcDetails = intent.getStringExtra("EPC") as String
            } else {
                errorReceptEpc("Erro ao receber TAG da tela anterior")
            }
        } catch (e: Exception) {
            errorReceptEpc("Erro ao receber TAG da tela anterior")
        }
    }

    private fun errorReceptEpc(error: String) {
        alertDefaulError(
            context = this,
            message = error,
            onClick = { finish() })
    }

    private fun searchDetaisEpc() {
        val sharedPreferences = CustomSharedPreferences(this)
        val idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        val token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        val body = BodyRecbimentoRfidPostDetalhesEpc(listNumSerie = listOf(epcDetails))
        viewModel.searchDetailEpc(token = token, idArmazem = idArmazem, body = body)
    }

    private fun observerViewModel() {
        viewModel.apply {
            errorResponse()
            sucessResponse()
            progress.observe(this@DetalheCodigoEpcActivity) {
                binding.progressInit.isVisible = it
            }
        }
    }

    private fun RecebimentoRfidViewModel.sucessResponse() {
        sucessReturnDetailsEpc.observe(this@DetalheCodigoEpcActivity) { data ->
            if (data.isNotEmpty()) {
                Log.e(TAG, "observerViewModel: $data")
                setInputs(data[0])
            }
        }
    }

    private fun setInputs(info: ResponseDetailsEpc) {
        try {
            binding.txtCodigoCor.text = info.corCdgo.toString()
            binding.txtCor.text = info.descricaoCor
            binding.txtUnidadeMedida.text = info.unidadeMedida
            binding.txtTamanho.text = info.tamanho
            binding.txtItem.text = info.nomeProduto
            binding.txtCodigoItem.text = info.idProduto.toString()
            binding.txtQuantidade.text = info.quantidade.toString()
        } catch (e: Exception) {
            errorReceptEpc("Erro ao setar dados na tela!\nsaia e tente novamente.")
        }
    }

    private fun RecebimentoRfidViewModel.errorResponse() {
        errorDb.observe(this@DetalheCodigoEpcActivity) { error ->
            errorReceptEpc(error)
        }
    }

    private fun setImagem() {
        val imageUrl =
            "https://images.tcdn.com.br/img/img_prod/1085400/tira_de_couro_sintetico_30mm_rolo_com_10_metros_preto_1007_1_56582ada6e81506208a488b900343e4d.jpeg"
//        Picasso.get()
//            .load(imageUrl)
//            .into(binding.imageEpc)
    }


    override fun finish() {
        super.finish()
        extensionBackActivityanimation()
    }

}