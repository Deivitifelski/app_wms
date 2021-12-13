package com.documentos.wms_beirario.ui.login

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityAlterarRotaActivityBinding
import com.example.coletorwms.constants.CustomSnackBarCustom

class AlterarRotaActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAlterarRotaActivityBinding
    private lateinit var textList: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAlterarRotaActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

//        mBinding.spinner.onItemSelectedListener = true
        mBinding.progressAlterarRota.visibility = View.INVISIBLE


    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        verificarRota9()
        adapterLIstSpinner()
    }

    private fun initToolbar() {
        val mToolbar = mBinding.toolbarAlterarRota
        mToolbar.setNavigationOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }


    private fun verificarRota9() {
        mBinding.buttonConfirmarRota.setOnClickListener {
            CustomSnackBarCustom().snackBarSucess(
                this,
                mBinding.layoutAlterarRota,
                "$textList selecionada!"
            )
            mBinding.progressAlterarRota.visibility = View.VISIBLE
            Handler().postDelayed({
                val intent = Intent()
                intent.putExtra("rota_alterada", textList)
                setResult(RESULT_OK, intent)
                onBackPressed()
            }, 1400)
        }
    }

    fun adapterLIstSpinner() {
        val mlistSpinner = listOf("PRODUÇÃO", "DESENVOLVIMENTO")
        var mSpinner = mBinding.spinner
        mSpinner.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, mlistSpinner)

        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val mRotaSelect = mBinding.spinner.selectedItemPosition
                if (mRotaSelect == 0) {
                    ServiceApi.baseUrl = ""
                    ServiceApi.baseUrl = "http://10.0.1.111:5001/wms/v1/"
                    textList = getString(com.documentos.wms_beirario.R.string.produce)
                } else {
                    ServiceApi.baseUrl = ""
                    ServiceApi.baseUrl = "http://10.0.1.111:5002/wms/v1/"
                    textList = getString(com.documentos.wms_beirario.R.string.development)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}



