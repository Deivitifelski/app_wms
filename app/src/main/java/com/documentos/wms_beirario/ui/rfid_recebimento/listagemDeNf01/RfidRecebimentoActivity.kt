package com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityRfidRecebimentoBinding
import com.documentos.wms_beirario.ui.rfid_recebimento.leituraEpc.RfidLeituraEpcActivity
import com.documentos.wms_beirario.ui.rfid_recebimento.listagemDeNf01.adapter.ListagemNfAdapterRfid
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity

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

//        adapterNf.updateData(listNf)
    }


    private fun cliqueButtonAvancar() {
        binding.buttonAvancar.setOnClickListener {
            extensionStartActivity(RfidLeituraEpcActivity())
        }
    }
}