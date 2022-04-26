package com.documentos.wms_beirario.ui.TaskType

import TypeTaskViewModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityTipoTarefaBinding
import com.documentos.wms_beirario.ui.TaskType.adapter.AdapterTipoTarefa
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity
import com.documentos.wms_beirario.ui.configuracoes.printer.SettingsActivity
import com.documentos.wms_beirario.ui.consultacodbarras.ConsultaCodBarrasActivity
import com.documentos.wms_beirario.ui.desmontagemdevolumes.DisassemblyVolActivity
import com.documentos.wms_beirario.ui.etiquetagem.EtiquetagemActivity
import com.documentos.wms_beirario.ui.inventory.InventarioActivity
import com.documentos.wms_beirario.ui.mountingVol.MountingVolActivity
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.MovimentacaoEntreEnderecosActivity
import com.documentos.wms_beirario.ui.picking.PickingActivity
import com.documentos.wms_beirario.ui.productionreceipt.ReceiptProductionActivity
import com.documentos.wms_beirario.ui.receipt.RecebimentoActivity
import com.documentos.wms_beirario.ui.reimpressao.ReimpressaoActivity
import com.documentos.wms_beirario.ui.separacao.SeparacaoActivity
import com.documentos.wms_beirario.ui.testes.Testes
import com.documentos.wms_beirario.utils.EnumTipoTarefaSigla
import com.documentos.wms_beirario.utils.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel

class TipoTarefaActivity : AppCompatActivity(R.layout.activity_tipo_tarefa) {
    private val mBinding: ActivityTipoTarefaBinding by viewBinding()
    private val mViewModel: TypeTaskViewModel by viewModel()
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mAdapter: AdapterTipoTarefa

    companion object{
      var mValidaAcess : Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mShared = CustomSharedPreferences(this)

        initData()
        initToolbar()
        initAdapter()
        mValidaAcess = true

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
                }
                EnumTipoTarefaSigla.DESMONTAGEM.sigla -> {
                    extensionStartActivity(DisassemblyVolActivity())
                }
                EnumTipoTarefaSigla.RECEBIMENTODEPRODUÇÃO.sigla -> {
                    extensionStartActivity(ReceiptProductionActivity())
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
                EnumTipoTarefaSigla.TESTEVELOCIDADE.sigla -> {
                    extensionStartActivity(Testes())
                }
                EnumTipoTarefaSigla.REIMPRESSAO.sigla -> {
                    extensionStartActivity(ReimpressaoActivity())
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        initViewModel()
    }

    private fun initToolbar() {
        val nameArmazem = intent.getStringExtra("NAME_ARMAZEM")
        mBinding.toolbar.subtitle = nameArmazem
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

    override fun finish() {
        super.finish()
        extensionStarBacktActivity(ArmazensActivity())
    }

}