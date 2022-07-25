package com.documentos.wms_beirario.ui.tipoTarefa

import TipoTarefaAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.ActivityTipoTarefaBinding
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import com.documentos.wms_beirario.repository.tipoTarefa.TypeTaskRepository
import com.documentos.wms_beirario.ui.armazenagem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.configuracoes.SettingsActivity
import com.documentos.wms_beirario.ui.consultaAuditoria.AuditoriaActivity
import com.documentos.wms_beirario.ui.consultacodbarras.ConsultaCodBarrasActivity
import com.documentos.wms_beirario.ui.etiquetagem.activitys.EtiquetagemActivity1
import com.documentos.wms_beirario.ui.inventory.activitys.init.InventarioActivity1
import com.documentos.wms_beirario.ui.mountingVol.activity.MountingActivity1
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.MovimentacaoEntreEnderecosActivity
import com.documentos.wms_beirario.ui.picking.activitys.PickingActivity1
import com.documentos.wms_beirario.ui.productionreceipt.ReceiptProductionActivity
import com.documentos.wms_beirario.ui.receipt.RecebimentoActivity
import com.documentos.wms_beirario.ui.reimpressao.ReimpressaoMainActivity
import com.documentos.wms_beirario.ui.separacao.activity.SeparacaoActivity1
import com.documentos.wms_beirario.ui.unmountingVolumes.activity.UnmountingVolumesActivity
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.EnumTipoTarefaSigla
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.mErroToastExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class TipoTarefaActivity : AppCompatActivity() {

    private val TAG = "TIPO_TAREFA"
    private lateinit var mBinding: ActivityTipoTarefaBinding
    private lateinit var mAdapter: TipoTarefaAdapter
    private lateinit var mViewModel: TipoTarefaViewModel
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var mShared: CustomSharedPreferences
    private var mIntentData: Boolean = false
    private lateinit var mToken: String
    private var mIdArmazem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTipoTarefaBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mShared = CustomSharedPreferences(this)
        mIdArmazem = mShared.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mToken = mShared.getString(CustomSharedPreferences.TOKEN).toString()
        Log.e(TAG, "DADOS RECEBIDOS SHARED --> TOKEN[${mToken}] || ID_ARMAZEM [${mIdArmazem}] ")
        initToolbar()
        initAdapter()
        initViewModel()
        setObeservable()
        initData()
    }

    private fun initData() {
        try {
            mBinding.nameUser.text =
                mShared.getString(CustomSharedPreferences.NAME_USER)?.replace("_", " ") ?: ""
            if (intent.extras != null) {
                val mData = intent.extras!!.getBoolean("A_WAREHOUSE")
                mIntentData = mData
                Log.e("TIPO_TAREFA", "initData --> $mIntentData ")
                if (mToken.isNullOrEmpty()) {
                    mErroToastExtension(
                        this,
                        "Não foi possivel acessar as tarefas\nTente novamente"
                    )
                } else {
                    mViewModel.getTask(mIdArmazem, mToken)
                }
            }
        } catch (e: Exception) {
            mErroToastExtension(this, "Erro ao receber dados da tela armazem!")
        }


    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, TipoTarefaViewModel.TarefaViewModelFactory(
                TypeTaskRepository()
            )
        )[TipoTarefaViewModel::class.java]
    }


    private fun initToolbar() {
        mToast = CustomSnackBarCustom()
        val nameArmazem = intent.getStringExtra("NAME_ARMAZEM")
        mBinding.toolbar.subtitle = "$nameArmazem [${getVersion()}]"
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**INICIA O ADAPTER E OS CLICKS PRA ONDE VAO -->*/
    private fun initAdapter() {
        mAdapter = TipoTarefaAdapter {
            when (it.sigla) {
                EnumTipoTarefaSigla.RECEBIMENTO.sigla -> {
                    extensionStartActivity(RecebimentoActivity())
                }
                EnumTipoTarefaSigla.ARMAZENAGEM.sigla -> {
                    mShared.saveInt(CustomSharedPreferences.ID_TAREFA, it.id)
                    extensionStartActivity(ArmazenagemActivity())
                }
//                EnumTipoTarefaSigla.EXPEDICAO.sigla -> {
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//                }
                EnumTipoTarefaSigla.SEPARAÇÃO.sigla -> {
                    extensionStartActivity(SeparacaoActivity1())
                }
                EnumTipoTarefaSigla.ETIQUETAGEM.sigla -> {
                    extensionStartActivity(EtiquetagemActivity1())
                }
                EnumTipoTarefaSigla.PICKING.sigla -> {
                    extensionStartActivity(PickingActivity1())
                }
                EnumTipoTarefaSigla.MOVIMENTAÇÃO.sigla -> {
                    extensionStartActivity(MovimentacaoEntreEnderecosActivity())
                }
                EnumTipoTarefaSigla.INVENTÁRIO.sigla -> {
                    extensionStartActivity(InventarioActivity1())
                }
                EnumTipoTarefaSigla.MONTAGEM.sigla -> {
                    extensionStartActivity(MountingActivity1())
                }
                EnumTipoTarefaSigla.DESMONTAGEM.sigla -> {
                    extensionStartActivity(UnmountingVolumesActivity())
                }
                EnumTipoTarefaSigla.RECEBIMENTODEPRODUÇÃO.sigla -> {
                    extensionStartActivity(ReceiptProductionActivity())
                }
//                EnumTipoTarefaSigla.NORMATIVA.sigla -> {
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//                }
                EnumTipoTarefaSigla.CONSULTACÓDIGODEBARRAS.sigla -> {
                    extensionStartActivity(ConsultaCodBarrasActivity())
                }
                EnumTipoTarefaSigla.CONFIGURAÇÃO.sigla -> {
                    extensionStartActivity(SettingsActivity())
                }

                EnumTipoTarefaSigla.REIMPRESSAO.sigla -> {
                    extensionStartActivity(ReimpressaoMainActivity())
                }

                EnumTipoTarefaSigla.AUDITORIA.sigla -> {
                    extensionStartActivity(AuditoriaActivity())
                }
            }
        }
    }


    private fun setObeservable() {
        mViewModel.mSucessShow.observe(this) { listTarefas ->
            mBinding.apply {
                //RECYCLERVIEW-->
                rvTipoTarefa.apply {
                    this.layoutManager = GridLayoutManager(this@TipoTarefaActivity, 2)
                    this.adapter = mAdapter
                }
            }
            mAdapter.update(listTarefas as MutableList<TipoTarefaResponseItem>)
        }

        mViewModel.mErrorHttpShow.observe(this) { erro ->
            vibrateExtension(500)
            mToast.snackBarErrorSimples(mBinding.root, erro)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            vibrateExtension(500)
            mToast.snackBarErrorSimples(mBinding.root, error)
        }

        mViewModel.mProgressShow.observe(this) { progress ->
            mBinding.progressInit.isVisible = progress
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("RETURT_DATA", mIntentData)
        Log.e("TIPO TAREFA", "onBackPressed --> $mIntentData ")
        setResult(RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}