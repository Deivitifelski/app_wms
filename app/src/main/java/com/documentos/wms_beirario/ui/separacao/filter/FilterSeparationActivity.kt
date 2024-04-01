package com.documentos.wms_beirario.ui.separacao.filter

import com.documentos.wms_beirario.ui.separacao.filter.adapterDoc.TypeDocAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityFilterSeparationBinding

class FilterSeparationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterSeparationBinding
    private var isShowMenuDoc: Boolean = false
    private var isShowMenuTrans: Boolean = false
    private lateinit var adapterDoc: TypeDocAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterSeparationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        clickDocumento()
        clickTransportadora()
        selectAllDoc()
        rotateArrowDocuments(0f, 180f)
        rotateArrowTransportadora(0f, 180f)

    }

    private fun selectAllDoc() {
        binding.checkAllDoc.setOnCheckedChangeListener { _, b ->
            if (b) {
                adapterDoc.selectAll()
            } else {
                adapterDoc.clearSelection()
            }
        }
    }

    private fun initConst() {
        adapterDoc = TypeDocAdapter {
            Log.e("->", "$it")
        }
        binding.recyclerDocumentos.apply {
            adapter = adapterDoc
            layoutManager = LinearLayoutManager(this@FilterSeparationActivity)
        }
        val list = listOf(
            "Separação",
            "Reestocagem",
            "Elaborada",
            "Negativa 1",
            "Negativa 2",
            "Negativa 3",
            "Negativa 4",
            "Negativa 5"
        )
        adapterDoc.updateDoc(list)
        Log.e("SELECIONADOS -->", "${adapterDoc.getSelectedItemsList()}")
    }

    private fun clickDocumento() {
        binding.menuDoc.setOnClickListener {
            if (!isShowMenuDoc) {
                isShowMenuDoc = true
                rotateArrowDocuments(180f, 0f)
                binding.linearDoc.visibility = View.VISIBLE
            } else {
                isShowMenuDoc = false
                rotateArrowDocuments(0f, 180f)
                binding.linearDoc.visibility = View.GONE
            }
        }
    }

    private fun clickTransportadora() {
        binding.menuTranportadora.setOnClickListener {
            if (!isShowMenuTrans) {
                isShowMenuTrans = true
                rotateArrowTransportadora(180f, 0f)
                binding.linearDoc.visibility = View.VISIBLE
            } else {
                isShowMenuTrans = false
                rotateArrowTransportadora(0f, 180f)
                binding.linearDoc.visibility = View.GONE
            }
        }
    }

    private fun rotateArrowDocuments(fromDegrees: Float, toDegrees: Float) {
        val rotation =
            ObjectAnimator.ofFloat(binding.iconArrowDoc, View.ROTATION, fromDegrees, toDegrees)
        rotation.duration = 200
        rotation.start()
    }


    private fun rotateArrowTransportadora(fromDegrees: Float, toDegrees: Float) {
        val rotation =
            ObjectAnimator.ofFloat(
                binding.iconArrowTransportadora,
                View.ROTATION,
                fromDegrees,
                toDegrees
            )
        rotation.duration = 200
        rotation.start()
    }


}