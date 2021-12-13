package com.documentos.wms_beirario.ui.Tarefas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityTipoTarefaBinding
import com.documentos.wms_beirario.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.extensions.extensionStartActivity
import com.documentos.wms_beirario.ui.Tarefas.adapter.AdapterTipoTarefa
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity
import com.documentos.wms_beirario.ui.configuracoes.SettingsActivity
import com.documentos.wms_beirario.ui.consultacodbarras.ConsultaCodBarrasActivity
import com.documentos.wms_beirario.ui.desmontagemdevolumes.DisassemblyVolActivity
import com.documentos.wms_beirario.ui.etiquetagem.EtiquetagemActivity
import com.documentos.wms_beirario.ui.inventario.InventarioActivity
import com.documentos.wms_beirario.ui.mountingVol.MountingVolActivity
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.MovimentacaoEntreEnderecosActivity
import com.documentos.wms_beirario.ui.picking.PickingActivity
import com.documentos.wms_beirario.ui.recebimento.RecebimentoActivity
import com.documentos.wms_beirario.ui.separacao.SeparacaoActivity
import com.documentos.wms_beirario.utils.EnumTipoTarefaSigla
import com.example.coletorwms.constants.CustomSnackBarCustom

class TipoTarefaActivity : AppCompatActivity(R.layout.activity_tipo_tarefa) {
    private val mBinding: ActivityTipoTarefaBinding by viewBinding()
    private lateinit var mViewModel: TipoTarefaViewModel
    private lateinit var mShared: CustomSharedPreferences
    private var mRetrofitService = ServiceApi.getInstance()
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
            when (it.sigla) {
                EnumTipoTarefaSigla.RECEBIMENTO.sigla -> {
                    extensionStartActivity(RecebimentoActivity())
                }
                EnumTipoTarefaSigla.ARMAZENAGEM.sigla -> {
                    mShared.saveInt(CustomSharedPreferences.ID_TAREFA, it.id)
                    extensionStartActivity(ArmazenagemActivity())
                }
                EnumTipoTarefaSigla.EXPEDICAO.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.SEPARAÇÃO.sigla -> {
                    extensionStartActivity(SeparacaoActivity())
                }
                EnumTipoTarefaSigla.ETIQUETAGEM.sigla -> {
                    extensionStartActivity(EtiquetagemActivity())
                }
                EnumTipoTarefaSigla.PICKING.sigla -> {
                    extensionStartActivity(PickingActivity())
                }
                EnumTipoTarefaSigla.MOVIMENTAÇÃO.sigla -> {
                    extensionStartActivity(MovimentacaoEntreEnderecosActivity())
                }
                EnumTipoTarefaSigla.INVENTÁRIO.sigla -> {
                    extensionStartActivity(InventarioActivity())
                }
                EnumTipoTarefaSigla.MONTAGEM.sigla -> {
                    extensionStartActivity(MountingVolActivity())
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.DESMONTAGEM.sigla -> {
                    extensionStartActivity(DisassemblyVolActivity())
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.RECEBIMENTODEPRODUÇÃO.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                EnumTipoTarefaSigla.NORMATIVA.sigla -> {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                EnumTipoTarefaSigla.CONSULTACÓDIGODEBARRAS.sigla -> {
                    extensionStartActivity(ConsultaCodBarrasActivity())
                }
                EnumTipoTarefaSigla.CONFIGURAÇÃO.sigla -> {
                    extensionStartActivity(SettingsActivity())
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
           extensionStarBacktActivity(ArmazensActivity())
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

    override fun onBackPressed() {
        super.onBackPressed()
        extensionStarBacktActivity(ArmazensActivity())
    }
}