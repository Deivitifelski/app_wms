package com.documentos.wms_beirario.ui.Tarefas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityTipoTarefaBinding
import com.documentos.wms_beirario.ui.Tarefas.adapter.AdapterTipoTarefa
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.configuracoes.SettingsActivity
import com.documentos.wms_beirario.ui.consultacodbarras.ConsultaCodBarrasActivity
import com.documentos.wms_beirario.ui.etiquetagem.EtiquetagemActivity
import com.documentos.wms_beirario.ui.inventario.InventarioActivity
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.MovimentacaoEntreEnderecosActivity
import com.documentos.wms_beirario.ui.recebimento.RecebimentoActivity
import com.documentos.wms_beirario.ui.separacao.SeparacaoActivity
import com.documentos.wms_beirario.utils.EnumTipoTarefaSigla
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom

class TipoTarefaActivity : AppCompatActivity(R.layout.activity_tipo_tarefa) {
    private val mBinding: ActivityTipoTarefaBinding by viewBinding()
    private lateinit var mViewModel: TipoTarefaViewModel
    private lateinit var mShared: CustomSharedPreferences
    private var mRetrofitService = RetrofitService.getInstance()
    private lateinit var mAdapter: AdapterTipoTarefa
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mShared = CustomSharedPreferences(this)

        mViewModel = ViewModelProvider(
            this,
            TipoTarefaViewModel.TipoTarefaViewModelFactory(TipoTarefaRepository(mRetrofitService))
        )[TipoTarefaViewModel::class.java]

        initData()
        initToolbar()
        initAdapter()
    }

    private fun initAdapter() {
        mAdapter = AdapterTipoTarefa {
            CustomMediaSonsMp3().somClick(this)
            when (it.sigla) {
                EnumTipoTarefaSigla.RECEBIMENTO.sigla -> {
                    startActivity(Intent(this, RecebimentoActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.ARMAZENAGEM.sigla -> {
                    mShared.saveInt(CustomSharedPreferences.ID_TAREFA, it.id)
                    startActivity(Intent(this, ArmazenagemActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.EXPEDICAO.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.SEPARAÇÃO.sigla -> {
                    startActivity(Intent(this, SeparacaoActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.ETIQUETAGEM.sigla -> {
                    startActivity(Intent(this, EtiquetagemActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.PICKING.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.MOVIMENTAÇÃO.sigla -> {
                    startActivity(Intent(this, MovimentacaoEntreEnderecosActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.INVENTÁRIO.sigla -> {
                    startActivity(Intent(this, InventarioActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.MONTAGEM.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.DESMONTAGEM.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.RECEBIMENTODEPRODUÇÃO.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.NORMATIVA.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                EnumTipoTarefaSigla.CONSULTACÓDIGODEBARRAS.sigla -> {
                    startActivity(Intent(this, ConsultaCodBarrasActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.CONFIGURAÇÃO.sigla -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun initData() {
        mViewModel.getTarefas()
    }

    private fun initViewModel() {
        mViewModel.mResponseSucess.observe(this, { listTarefas ->
            mBinding.apply {
                //RECYCLERVIEW-->
                rvTipoTarefa.apply {
                    this.layoutManager = GridLayoutManager(this@TipoTarefaActivity, 2)
                    this.adapter = mAdapter
                }
            }
            mAdapter.update(listTarefas)
        })

        mViewModel.mResponseError.observe(this, { erro ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding.layout, erro.toString())
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}