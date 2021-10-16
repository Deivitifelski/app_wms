package com.documentos.wms_beirario.ui.Tarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityTipoTarefaBinding
import com.documentos.wms_beirario.ui.Tarefas.adapter.AdapterTipoTarefa
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.armazengem.fragment.ArmazenagemFragment_01
import com.documentos.wms_beirario.utils.EnumTipoTarefaSigla
import com.example.coletorwms.constants.CustomMediaSonsMp3

class TipoTarefaActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTipoTarefaBinding
    private lateinit var mViewModel: TipoTarefaViewModel
    private lateinit var mShared: CustomSharedPreferences
    private var mRetrofitService = RetrofitService.getInstance()
    private lateinit var mToken: String
    private var mIdArmazem: Int = 0
    private lateinit var mAdapter: AdapterTipoTarefa
    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityTipoTarefaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mShared = CustomSharedPreferences(this)

        mViewModel = ViewModelProvider(
            this,
            TipoTarefaViewModelFactory(TipoTarefaRepository(mRetrofitService))
        ).get(TipoTarefaViewModel::class.java)

        initShared()
        initData()
        initToolbar()
        initAdapter()
    }

    private fun initAdapter() {
        mAdapter = AdapterTipoTarefa {
            CustomMediaSonsMp3().somClick(this)
            when (it.sigla) {
                EnumTipoTarefaSigla.RECEBIMENTO.sigla -> {

                }
                EnumTipoTarefaSigla.ARMAZENAGEM.sigla -> {
                  mShared.saveInt(CustomSharedPreferences.ID_TAREFA,it.id)
                    startActivity(Intent(this, ArmazenagemActivity::class.java))
                }
                EnumTipoTarefaSigla.EXPEDICAO.sigla -> {

                }
                EnumTipoTarefaSigla.SEPARAÇÃO.sigla -> {

                }
                EnumTipoTarefaSigla.ETIQUETAGEM.sigla -> {

                }
                EnumTipoTarefaSigla.PICKING.sigla -> {

                }
                EnumTipoTarefaSigla.MOVIMENTAÇÃO.sigla -> {

                }
                EnumTipoTarefaSigla.INVENTÁRIO.sigla -> {

                }
                EnumTipoTarefaSigla.MONTAGEM.sigla -> {

                }
                EnumTipoTarefaSigla.DESMONTAGEM.sigla -> {

                }
                EnumTipoTarefaSigla.RECEBIMENTODEPRODUÇÃO.sigla -> {

                }
                EnumTipoTarefaSigla.NORMATIVA.sigla -> {

                }

                EnumTipoTarefaSigla.CONSULTACÓDIGODEBARRAS.sigla -> {

                }
                EnumTipoTarefaSigla.CONFIGURAÇÃO.sigla -> {

                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        initViewModel()
    }

    private fun initToolbar() {
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun initShared() {
        mToken = mShared.getString(CustomSharedPreferences.TOKEN)!!
        mIdArmazem = mShared.getInt(CustomSharedPreferences.ID_ARMAZEM)!!
    }

    private fun initData() {
        mViewModel.getTarefas(mToken, mIdArmazem)
    }

    private fun initViewModel() {
        mViewModel.mResponseSucess.observe(this, Observer { listTarefas ->
            mBinding.apply {
                //RECYCLERVIEW-->
                rvTipoTarefa.apply {
                    this.layoutManager = GridLayoutManager(this@TipoTarefaActivity, 2)
                    this.adapter = mAdapter
                }
            }
            mAdapter.update(listTarefas)
        })

        mViewModel.mResponseError.observe(this, Observer { erro ->
            Toast.makeText(this, erro.toString(), Toast.LENGTH_SHORT).show()
        })

    }
}