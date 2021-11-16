package com.documentos.wms_beirario.ui.armazens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityArmazensBinding
import com.documentos.wms_beirario.repository.ArmazensRepository
import com.documentos.wms_beirario.ui.Tarefas.TipoTarefaActivity
import com.documentos.wms_beirario.ui.armazens.adapter.AdapterArmazens

class ArmazensActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityArmazensBinding
    private lateinit var mViewModel: ArmazensViewModel
    private lateinit var mAdapter: AdapterArmazens
    private var retrofitService = RetrofitService.getInstance()
    private lateinit var mToken: String
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArmazensBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mSharedPreferences = CustomSharedPreferences(this)
        mViewModel =
            ViewModelProvider(
                this,
                ArmazensViewModel.ArmazensViewModelFactory(ArmazensRepository(retrofitService))
            ).get(
                ArmazensViewModel::class.java
            )
        initToolbar()
        initData()

    }


    private fun initToolbar() {
        val toolbar = mBinding.toolbarArmazem
        toolbar.setNavigationOnClickListener {
            onBackPressed()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initClickStartActivity() {
        mAdapter = AdapterArmazens {
            mSharedPreferences.saveInt(CustomSharedPreferences.ID_ARMAZEM, it.id)
            startActivity(Intent(this,TipoTarefaActivity::class.java))
        }
    }

    private fun initData() {
        mToken = mSharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        mViewModel.getArmazens(mToken)
        mViewModel.mShowSucess.observe(this, Observer { response ->
            mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
            if (response.isEmpty()) {
                Toast.makeText(this, "Lista Vazia", Toast.LENGTH_SHORT).show()
            } else {
                initClickStartActivity()
                mBinding.rvArmazem.apply {
                    this.layoutManager = LinearLayoutManager(this@ArmazensActivity)
                    this.adapter = mAdapter
                }
                mAdapter.update(response)
            }
        })
        mViewModel.mShowErrorUser.observe(this, Observer { response ->
            mBinding.progressBarInitArmazens.visibility = View.INVISIBLE
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        })

    }


}