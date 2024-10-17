package com.documentos.wms_beirario.ui.rfid_recebimento.listagemNf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityRfidRecebimentoBinding
import com.documentos.wms_beirario.model.recebimentoRfid.RecebimentoRfid
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.listagemNf.adapter.ListagemNfAdapterRfid
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class RfidRecebimentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidRecebimentoBinding
    private lateinit var adapterNf: ListagemNfAdapterRfid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidRecebimentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setDataRecyclerView()
        cliqueButtonAvancar()
    }

    private fun setupAdapter() {
        adapterNf = ListagemNfAdapterRfid()
        binding.rvNfRecebimentoRfid.apply {
            adapter = adapterNf
            layoutManager = LinearLayoutManager(this@RfidRecebimentoActivity)
        }
    }

    private fun setDataRecyclerView() {
        val listNf = listOf(
            RecebimentoRfid(
                nf = "NF: 123456",
                filial = "Filial: 17",
                remessa = "Serie: 12233",
                qtdEtiquetas = 123,
                conferida = true
            ), RecebimentoRfid(
                nf = "NF: 5675", filial = "Filial: 17", remessa = "Serie: 2223", qtdEtiquetas = 7
            ), RecebimentoRfid(
                nf = "NF: 0987", filial = "Filial: 17", remessa = "Serie: 6789", qtdEtiquetas = 78, conferida = true
            ), RecebimentoRfid(
                nf = "NF: 467", filial = "Filial: 17", remessa = "Serie: 143", qtdEtiquetas = 10, conferida = false
            ), RecebimentoRfid(
                nf = "NF: 123456", filial = "Filial: 17", remessa = "Serie: 0877", qtdEtiquetas = 200
            )
        )
        adapterNf.updateData(listNf)
    }


    private fun cliqueButtonAvancar() {
        binding.buttonAvancar.setOnClickListener {
            extensionStartActivity(RfidLeituraEpcActivity())
        }
    }
}