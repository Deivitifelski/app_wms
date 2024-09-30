package com.documentos.wms_beirario.ui.tipoTarefa

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
import com.documentos.wms_beirario.databinding.LayoutOpcaoMovimentacaoBinding
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem
import com.documentos.wms_beirario.repository.tipoTarefa.TypeTaskRepository
import com.documentos.wms_beirario.ui.armazenagem.ArmazenagemActivity
import com.documentos.wms_beirario.ui.auditoriaEstoque.views.AuditoriaEstoqueActivity
import com.documentos.wms_beirario.ui.boardingConference.BoardingConferenceActivity
import com.documentos.wms_beirario.ui.configuracoes.SettingsActivity
import com.documentos.wms_beirario.ui.consultaAuditoria.AuditoriaActivity
import com.documentos.wms_beirario.ui.consultacodbarras.ConsultaCodBarrasActivity
import com.documentos.wms_beirario.ui.etiquetagem.activitys.EtiquetagemActivity1
import com.documentos.wms_beirario.ui.inventory.activitys.init.InventarioActivity1
import com.documentos.wms_beirario.ui.mountingVol.activity.MountingActivity1
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.activitys.MovimentacaoEnderecosActivity1
import com.documentos.wms_beirario.ui.picking.activitys.PickingActivity1
import com.documentos.wms_beirario.ui.qualityControl.activity.QualityControlActivity
import com.documentos.wms_beirario.ui.receipt.RecebimentoActivity
import com.documentos.wms_beirario.ui.receiptProduction.acrivitys.init.RecebimentoDeProducaoActivity1
import com.documentos.wms_beirario.ui.reimpressao.ReimpressaoMainActivity
import com.documentos.wms_beirario.ui.reservationByRequest.ReservationbyrequestActivity
import com.documentos.wms_beirario.ui.separacao.activity.SeparacaoActivity1
import com.documentos.wms_beirario.ui.tipoTarefa.adapter.TipoTarefaAdapter
import com.documentos.wms_beirario.ui.unmountingVolumes.activity.UnmountingVolumesActivity
import com.documentos.wms_beirario.ui.volumeMovement.VolumeMovementActivity
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.EnumTipoTarefaSigla
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.helloUser
import com.documentos.wms_beirario.utils.extensions.toastError
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TipoTarefaActivity : AppCompatActivity() {

    private val TAG = "TIPO_TAREFA"
    private lateinit var mBinding: ActivityTipoTarefaBinding
    private lateinit var mAdapter: TipoTarefaAdapter
    private lateinit var mViewModel: TipoTarefaViewModel
    private lateinit var mToast: CustomSnackBarCustom
    private lateinit var sharedPreferences: CustomSharedPreferences
    private var mIntentData: Boolean = false
    private lateinit var mToken: String
    private var idArmazem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTipoTarefaBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        sharedPreferences = CustomSharedPreferences(this)
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mToken = sharedPreferences.getString(CustomSharedPreferences.TOKEN).toString()
        Log.e(TAG, "DADOS RECEBIDOS SHARED --> TOKEN[${mToken}] || ID_ARMAZEM [${idArmazem}] ")
        initToolbar()
        initAdapter()
        initViewModel()
        setObeservable()
        initData()
    }

    private fun initData() {
        try {
            if (intent.extras != null) {
                val mData = intent.extras!!.getBoolean("A_WAREHOUSE")
                mIntentData = mData
                Log.e("TIPO_TAREFA", "initData --> $mIntentData ")
                if (mToken.isNullOrEmpty()) {
                    toastError(
                        this,
                        "Não foi possivel acessar as tarefas\nTente novamente"
                    )
                } else {
                    mViewModel.getTask(idArmazem, mToken)
                }
            }
        } catch (e: Exception) {
            toastError(this, "Erro ao receber dados da tela armazem!")
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
        mBinding.txtHello.text = helloUser()
        val nameArmazem = intent.getStringExtra("NAME_ARMAZEM")
        mBinding.toolbar.subtitle = "$nameArmazem [${getVersion()}]"
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**INICIA O ADAPTER E OS CLICKS PRA ONDE VAO -->*/
    private fun initAdapter() {
        mAdapter = TipoTarefaAdapter {
            when (it.id) {
                EnumTipoTarefaSigla.RECEBIMENTO.id -> {
                    extensionStartActivity(RecebimentoActivity())
                }

                EnumTipoTarefaSigla.ARMAZENAGEM.id -> {
                    sharedPreferences.saveInt(CustomSharedPreferences.ID_TAREFA, it.id)
                    extensionStartActivity(ArmazenagemActivity())
                }

                EnumTipoTarefaSigla.SEPARAÇÃO.id -> {
                    extensionStartActivity(SeparacaoActivity1())
                }

                EnumTipoTarefaSigla.ETIQUETAGEM.id -> {
                    extensionStartActivity(EtiquetagemActivity1())
                }

                EnumTipoTarefaSigla.PICKING.id -> {
                    extensionStartActivity(PickingActivity1())
                }

                EnumTipoTarefaSigla.MOVIMENTAÇÃO.id -> {
                    showModal()
                }

                EnumTipoTarefaSigla.INVENTÁRIO.id -> {
                    extensionStartActivity(InventarioActivity1())
                }

                EnumTipoTarefaSigla.MONTAGEM.id -> {
                    extensionStartActivity(MountingActivity1())
                }

                EnumTipoTarefaSigla.DESMONTAGEM.id -> {
                    extensionStartActivity(UnmountingVolumesActivity())
                }

                EnumTipoTarefaSigla.RECEBIMENTODEPRODUÇÃO.id -> {
                    extensionStartActivity(RecebimentoDeProducaoActivity1())
                }

                EnumTipoTarefaSigla.CONSULTACÓDIGODEBARRAS.id -> {
                    extensionStartActivity(ConsultaCodBarrasActivity())
                }

                EnumTipoTarefaSigla.CONFIGURAÇÃO.id -> {
                    extensionStartActivity(SettingsActivity())
                }

                EnumTipoTarefaSigla.REIMPRESSAO.id -> {
                    extensionStartActivity(ReimpressaoMainActivity())
                }

                EnumTipoTarefaSigla.AUDITORIADEESTOQUE.id -> {
                    extensionStartActivity(AuditoriaEstoqueActivity())
                }

                EnumTipoTarefaSigla.AUDITORIA.id -> {
                    extensionStartActivity(AuditoriaActivity())
                }

                EnumTipoTarefaSigla.RESERVADEPEDIDO.id -> {
                    extensionStartActivity(ReservationbyrequestActivity())
                }

                EnumTipoTarefaSigla.QUALIDADE.id -> {
                    extensionStartActivity(QualityControlActivity())
                }

                EnumTipoTarefaSigla.CONFERENCIADEEBARQUE.id -> {
                    extensionStartActivity(BoardingConferenceActivity())
                }
            }
        }
    }

    private fun showModal() {
        val binding = LayoutOpcaoMovimentacaoBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(binding.root)
            .create()
        binding.movimentacaoEnderecosButton.setOnClickListener {
            extensionStartActivity(MovimentacaoEnderecosActivity1())
            dialog.dismiss()
        }
        binding.movimentacaoVolumesButton.setOnClickListener {
            extensionStartActivity(VolumeMovementActivity())
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun setObeservable() {
        mViewModel.mSucessShow.observe(this) { listTarefas ->
            mBinding.apply {
                txtHello.isVisible = true
                rvTipoTarefa.apply {
                    this.layoutManager = GridLayoutManager(this@TipoTarefaActivity, 2)
                    this.adapter = mAdapter
                }
            }
            mBinding.nameUser.text =
                sharedPreferences.getString(CustomSharedPreferences.NAME_USER)?.replace("_", " ")
                    ?: ""
            mAdapter.update(listTarefas as MutableList<TipoTarefaResponseItem>)
        }

        mViewModel.mErrorHttpShow.observe(this) { erro ->
            mBinding.txtHello.isVisible = false
            vibrateExtension(500)
            mToast.snackBarErrorSimples(mBinding.root, erro)
        }

        mViewModel.mErrorAllShow.observe(this) { error ->
            mBinding.txtHello.isVisible = false
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

//    private fun buildIPCSafeReceiver(resultReceiver: ResultReceiver): ResultReceiver {
//        val parcel = Parcel.obtain()
//        resultReceiver.writeToParcel(parcel, 0)
//        parcel.setDataPosition(0)
//        val receiverfor = ResultReceiver.CREATOR.createFromParcel(parcel)
//        parcel.recycle()
//        return receiverfor
//    }

}