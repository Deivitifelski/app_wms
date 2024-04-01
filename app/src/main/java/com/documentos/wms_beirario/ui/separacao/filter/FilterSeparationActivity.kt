package com.documentos.wms_beirario.ui.separacao.filter

import com.documentos.wms_beirario.ui.separacao.filter.adapterDoc.TypeDocAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.ActivityFilterSeparationBinding
import com.documentos.wms_beirario.ui.separacao.filter.adapterTransportadora.TypeTransportadoraAdapter
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar

class FilterSeparationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterSeparationBinding
    private var isShowMenuDoc: Boolean = false
    private var isShowMenuTrans: Boolean = false
    private lateinit var adapterDoc: TypeDocAdapter
    private lateinit var adapterTransportadora: TypeTransportadoraAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterSeparationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        clickDocumento()
        clickTransportadora()
        selectAllDoc()
        selectAllTrans()
        rotateArrowDocuments(0f, 180f)
        rotateArrowTransportadora(0f, 180f)
        clickAplicar()

    }

    private fun clickAplicar() {
        binding.buttonAplicar.setOnClickListener {
            binding.buttonAplicar.visibility = View.INVISIBLE
            binding.progressAplicar.visibility = View.VISIBLE
            Handler().postDelayed({
                setResult(RESULT_OK)
                Log.e("-->", "clickAplicar: ${adapterDoc.getSelectedItemsList()}")
                Log.e("-->", "clickAplicar: ${adapterTransportadora.getSelectedItemsList()}")
                finish()
                binding.buttonAplicar.visibility = View.VISIBLE
                binding.progressAplicar.visibility = View.INVISIBLE
            }, 1400)
        }
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

    private fun selectAllTrans() {
        binding.checkAllTrans.setOnCheckedChangeListener { _, b ->
            if (b) {
                adapterTransportadora.selectAll()
            } else {
                adapterTransportadora.clearSelection()
            }
        }
    }

    private fun initConst() {
        binding.toolbar9.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                finish()
            }
        }
        adapterDoc = TypeDocAdapter {
            Log.e("->", "$it")
        }
        adapterTransportadora = TypeTransportadoraAdapter {
            Log.e("->", "$it")
        }
        binding.recyclerDocumentos.apply {
            adapter = adapterDoc
            layoutManager = LinearLayoutManager(this@FilterSeparationActivity)
        }
        binding.recyclerTransportadora.apply {
            adapter = adapterTransportadora
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
        val listTra = listOf(
            "Translovato",
            "Mil",
            "Papaleguas",
            "zoom 1",
            "zoom 2",
            "zoom 3",
            "zoom 4",
            "zoom 5"
        )
        adapterDoc.updateDoc(list)
        adapterTransportadora.updateDoc(listTra)
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
                binding.linearTrans.visibility = View.VISIBLE
            } else {
                isShowMenuTrans = false
                rotateArrowTransportadora(0f, 180f)
                binding.linearTrans.visibility = View.GONE
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