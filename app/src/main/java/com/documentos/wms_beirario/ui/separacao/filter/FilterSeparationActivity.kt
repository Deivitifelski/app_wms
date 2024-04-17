package com.documentos.wms_beirario.ui.separacao.filter

import com.documentos.wms_beirario.ui.separacao.filter.adapterDoc.TypeDocAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityFilterSeparationBinding
import com.documentos.wms_beirario.model.separation.filtros.ItemDocTrans
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.filter.adapterTransportadora.TypeTransportadoraAdapter
import com.documentos.wms_beirario.ui.separacao.filter.viewModel.SeparacaoFilterViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.alertDefaulError
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastDefault

class FilterSeparationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterSeparationBinding
    private lateinit var viewModel: SeparacaoFilterViewModel
    private var isShowMenuDoc: Boolean = false
    private var isShowMenuTrans: Boolean = false
    private lateinit var adapterDoc: TypeDocAdapter
    private lateinit var dialog: Dialog
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var adapterTrans: TypeTransportadoraAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterSeparationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initConst()
        getListFiles()
        clickFiles()
        clickTransportadora()
        selectAllDoc()
        selectAllTrans()
        rotateArrowDocuments(0f, 180f)
        rotateArrowTransportadora(0f, 180f)
        clickAplicar()
        observer()

    }

    private fun getListTransportadora() {
        val token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)!!
        val idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        viewModel.getListTrans(token = token, idArmazem = idArmazem)
    }

    private fun observer() {
        viewModel.apply {
            /**Progress -->*/
            progressShow.observe(this@FilterSeparationActivity) {
                if (it) {
                    dialog.show()
                } else {
                    dialog.hide()
                }
            }
            /**Resultado da busca de docuemtos -->*/
            sucessShow.observe(this@FilterSeparationActivity) { files ->
                if (files.isNotEmpty()) {
                    adapterDoc.updateDoc(files)
                } else {
                    alertDefaulError(
                        this@FilterSeparationActivity,
                        message = "Nenhum documento retornado",
                        onClick = {
                            finish()
                        })
                }
            }
            /**Erro da busca de docuemtos -->*/
            errorShow.observe(this@FilterSeparationActivity) {
                alertDefaulError(this@FilterSeparationActivity, message = it, onClick = {})
            }
            /**Resultado da busca de docuemtos -->*/
            sucessTransShow.observe(this@FilterSeparationActivity) { trans ->
                if (trans.isNotEmpty()) {
                    adapterTrans.updateDoc(trans)
                } else {
                    alertDefaulError(
                        this@FilterSeparationActivity,
                        message = "Nenhum documento retornado",
                        onClick = {})
                }
            }
        }
    }

    private fun getListFiles() {
        val token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)!!
        val idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        viewModel.getListFiles(token = token, idArmazem = idArmazem)
    }

    private fun clickAplicar() {
        binding.buttonAplicar.setOnClickListener {
            binding.buttonAplicar.visibility = View.INVISIBLE
            binding.progressAplicar.visibility = View.VISIBLE
            Handler().postDelayed({
                val intent = Intent()
                intent.putExtra("DOC", ItemDocTrans(adapterDoc.getSelectedItemsList()))
                intent.putExtra("TRANS", ItemDocTrans(adapterTrans.getSelectedItemsList()))
                setResult(RESULT_OK, intent)
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
                adapterTrans.selectAll()
            } else {
                adapterTrans.clearSelection()
            }
        }
    }

    private fun initConst() {
        dialog = CustomAlertDialogCustom().progress(this, "Buscando as informações...")
        dialog.hide()
        sharedPreferences = CustomSharedPreferences(this)
        viewModel = ViewModelProvider(
            this, SeparacaoFilterViewModel.SeparacaoFilterViewModelFactory(
                SeparacaoRepository()
            )
        )[SeparacaoFilterViewModel::class.java]

        binding.toolbar9.apply {
            subtitle = getVersionNameToolbar()
            setNavigationOnClickListener {
                finish()
            }
        }
        adapterDoc = TypeDocAdapter { item ->
            searchDataItemSaidaNf(item)
        }
        adapterTrans = TypeTransportadoraAdapter {

        }

        binding.recyclerDocumentos.apply {
            adapter = adapterDoc
            layoutManager = LinearLayoutManager(this@FilterSeparationActivity)
        }
        binding.recyclerTransportadora.apply {
            adapter = adapterTrans
            layoutManager = LinearLayoutManager(this@FilterSeparationActivity)
        }
    }

    private fun searchDataItemSaidaNf(item: List<String>) {
        val item = item.firstOrNull { it == "7" }
        if (item != null) {
            getListTransportadora()
            toastDefault(this, "Selecione as transportadoras")
        } else {
            adapterTrans.clearSelectionSaidaNf()
            toastDefault(this, "Listas transportadoras foram limpas")
        }
    }

    private fun clearListTransportadoras() {
        adapterTrans.clearSelectionSaidaNf()
    }

    private fun clickFiles() {
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

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

}