package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.model.recebimentoRfid.LeituraRfidEpc
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.alertConfirmation
import com.documentos.wms_beirario.utils.extensions.alertDefaulSimplesError
import com.documentos.wms_beirario.utils.extensions.alertMessageSucessAction
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.bind
import kotlin.random.Random

class RfidLeituraEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupList(size = 10, tipoLeitura = "R")
        cliqueChips()
        clickButtonLimpar()
        clickButtonFinalizar()
    }

    private fun clickButtonFinalizar() {
        binding.buttonFinalizar.setOnClickListener {
            alertConfirmation(
                message = "Deseja realizar o recebimento?",
                actionNo = {},
                actionYes = {
                    alertMessageSucessAction(
                        message = "Recebimento realizado com sucesso!",
                        action = {
                            finish()
                            extensionBackActivityanimation()
                        })
                }
            )
        }
    }

    private fun clickButtonLimpar() {
        binding.buttonClear.setOnClickListener {
            alertConfirmation(
                message = "Deseja limpar as leituras e iniciar novamente?",
                actionNo = {},
                actionYes = {
                    setupList(size = 10, tipoLeitura = "R")
                }
            )
        }
    }


    private fun gerarEpcAleatorio(): String {
        val epc = Random.nextBytes(12) // 12 bytes = 24 caracteres hexadecimais
        return epc.joinToString("") { "%02x".format(it) }
    }

    private fun setupList(size: Int, tipoLeitura: String) {
        val list = mutableListOf<LeituraRfidEpc>()
        for (i in 1..size) {
            val epc = gerarEpcAleatorio()
            list.add(
                LeituraRfidEpc(
                    tag = epc,
                    descricao = "cód.i - desc - cód.cor - desc - tam - unid.med - qtde",
                    tipoLeitura = tipoLeitura
                )
            )
        }

        adapterLeituras.updateData(list)
        setTextInput(list)
    }

    private fun setTextInput(list: MutableList<LeituraRfidEpc>) {
        binding.apply {
            textQtdLeituras.text = "12 / ${10}"
            chipRelacionados.text = "Relacionados - 10"
            chipFaltando.text = "Faltando - 2"
            chipEncontrados.text = "Encontrados - 8"
            chipNaoRelacionado.text = "Não relacionados - 2"
        }
    }

    private fun setupAdapter() {
        adapterLeituras = LeituraRfidAdapter()
        binding.rvItemEpcRecebimento.apply {
            adapter = adapterLeituras
            layoutManager = LinearLayoutManager(this@RfidLeituraEpcActivity)
        }
    }


    private fun cliqueChips() {
        binding.chipRelacionados.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                when (chip.id) {
                    R.id.chip_faltando -> {
                        setupList(size = 2, tipoLeitura = "F")
                    }

                    R.id.chip_encontrados -> {
                        setupList(size = 8, tipoLeitura = "E")
                    }

                    R.id.chip_nao_relacionado -> {
                        setupList(size = 2, tipoLeitura = "NR")
                    }

                    R.id.chip_relacionados -> {
                        setupList(size = 10, tipoLeitura = "R")
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}