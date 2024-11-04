package com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityDetalheCodigoEpcBinding
import com.documentos.wms_beirario.model.recebimentoRfid.BodyRecbimentoRfidPostDetalhesEpc
import com.documentos.wms_beirario.model.recebimentoRfid.ResponseSearchDetailsEpc
import com.documentos.wms_beirario.repository.recebimentoRfid.RecebimentoRfidRepository
import com.documentos.wms_beirario.ui.rfid_recebimento.viewModel.RecebimentoRfidViewModel
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.convertData
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.toastDefault
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.File
import java.util.EnumSet

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
        setImagem()
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

    private fun setInputs(info: ResponseSearchDetailsEpc) {
        try {
            binding.txtCor.text = "${info.corCdgo} | ${info.descricaoCor}"
            binding.txtUnidadeMedida.text = info.unidadeMedida
            binding.txtNfTransferencia.text = info.notaFiscal
            binding.txtTamanho.text = info.tamanho
            binding.txtItem.text = "${info.idProduto} | ${info.nomeProduto}"
            binding.txtQuantidade.text = info.quantidade.toString()
            binding.txtDataEmissao.text = info.dataEmissao.convertData(info.dataEmissao)
            binding.txtDestino.text = "Filial: ${info.filialDestino}"
            binding.txtOrigem.text = "Filial: ${info.filialEmitente}"
        } catch (e: Exception) {
            errorReceptEpc("Erro ao setar informações na tela!Dados retornados podem ter vindo nulos, saia e tente novamente.")
        }
    }

    private fun RecebimentoRfidViewModel.errorResponse() {
        errorDb.observe(this@DetalheCodigoEpcActivity) { error ->
            errorReceptEpc(error)
        }
    }

    private fun setImagem() {
        val ftpClient = FTPClient()
        try {
            // Conectar ao servidor FTP
            ftpClient.connect("10.0.0.27")
            val loginSuccess = ftpClient.login("deiviti_felski", "Pa!pe2024")

            if (loginSuccess) {
                ftpClient.enterLocalPassiveMode()
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

                // Abrir o InputStream para o arquivo
                val inputStream = ftpClient.retrieveFileStream("/imagens_material/168286.gif")

                if (inputStream != null) {
                    // Carregar o Bitmap a partir do InputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageEpc.setImageBitmap(bitmap)
                    Log.i(TAG, "Imagem carregada com sucesso via FTP")
                } else {
                    Log.e(TAG, "InputStream é nulo. Verifique o caminho do arquivo.")
                }

                // Sempre finalize a transferência de arquivos
                ftpClient.completePendingCommand()
            } else {
                Log.e(TAG, "Falha ao realizar login no FTP")
            }

            // Fazer logout
            ftpClient.logout()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar imagem: ${e.localizedMessage}")
            binding.imageEpc.setImageResource(R.drawable.icon_image_detalhes)
        } finally {
            try {
                if (ftpClient.isConnected) {
                    ftpClient.disconnect()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao desconectar: ${e.localizedMessage}")
            }
        }
    }


    override fun finish() {
        super.finish()
        extensionBackActivityanimation()
    }

}