package com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityRfidLeituraEpcBinding
import com.documentos.wms_beirario.model.recebimentoRfid.LeituraRfidEpc
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.adapter.LeituraRfidAdapter
import com.google.android.material.chip.Chip
import kotlin.random.Random

class RfidLeituraEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidLeituraEpcBinding
    private lateinit var adapterLeituras: LeituraRfidAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidLeituraEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupList()
        cliqueChips()
    }



    private fun gerarEpcAleatorio(): String {
        val epc = Random.nextBytes(12) // 12 bytes = 24 caracteres hexadecimais
        return epc.joinToString("") { "%02x".format(it) }
    }

    private fun setupList() {
        val list = mutableListOf<LeituraRfidEpc>()
        for (i in 1..10) {
            val epc = gerarEpcAleatorio()
            list.add(
                LeituraRfidEpc(
                    tag = epc,
                    descricao = "cód.i - desc - cód.cor - desc - tam - unid.med - qtde"
                )
            )
        }
        adapterLeituras.updateData(list)
    }

    private fun setupAdapter() {
        adapterLeituras = LeituraRfidAdapter()
        binding.rvItemEpcRecebimento.apply {
            adapter = adapterLeituras
            layoutManager = LinearLayoutManager(this@RfidLeituraEpcActivity)
        }
    }


    private fun cliqueChips() {
            binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                if (checkedIds.isNotEmpty()) {
                    val chipId = checkedIds[0]
                    val chip = group.findViewById<Chip>(chipId)
                    when (chip.id) {
                        R.id.chip_faltando -> {

                        }
                        R.id.chip_encontrados -> {

                        }
                        R.id.chip_registrados -> {

                        }
                        R.id.chip_nao_relacionados -> {

                        }
                    }
                } else {
                    Toast.makeText(this, "Nenhum Chip selecionado", Toast.LENGTH_SHORT).show()
                }
            }
    }
}