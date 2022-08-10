package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.fragments

import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.EndMovementViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentEndMovement2Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementAddTask
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementFinishAndress
import com.documentos.wms_beirario.model.movimentacaoentreenderecos.MovementReturnItemClickMov
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter2Movimentacao
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import com.documentos.wms_beirario.utils.extensions.vibrateExtension


class EndMovementFragment2 : Fragment() {

    private lateinit var mToken: String
    private var mIdArmazem: Int = 0
    private lateinit var mAdapter: Adapter2Movimentacao
    private lateinit var mViewModel: EndMovementViewModel
    private lateinit var mShared: CustomSharedPreferences
    private var _binding: FragmentEndMovement2Binding? = null
    private val mBinding get() = _binding!!
    private val mArgs: EndMovementFragment2Args by navArgs()
    private lateinit var mProgress: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEndMovement2Binding.inflate(inflater, container, false)
        initConst()
        setRecyclerView()
        initViewModel()
        AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, visibility = false)
        getShared()
        callApi()
        setupObservable()
        setToolbar()
        clickButtonFinish()
        initEditAddTask()
        setupSwipe()
        return mBinding.root
    }

    private fun initConst() {
        mShared = CustomSharedPreferences(requireContext())
        mProgress = CustomAlertDialogCustom().progress(requireContext())
        mProgress.hide()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, EndMovementViewModel.Mov2ViewModelFactory(
                MovimentacaoEntreEnderecosRepository()
            )
        )[EndMovementViewModel::class.java]
    }

    private fun setToolbar() {
        mBinding.toolbarMov2.apply {
            setNavigationOnClickListener {
                CustomMediaSonsMp3().somClick(requireContext())
                findNavController().navigateUp()
            }
        }
    }

    private fun setRecyclerView() {
        mAdapter = Adapter2Movimentacao()
        mBinding.rvMov2.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun getShared() {
        mShared.apply {
            mToken = getString(CustomSharedPreferences.TOKEN).toString()
            mIdArmazem = getInt(CustomSharedPreferences.ID_ARMAZEM)
        }

    }

    /**VERIFICA SE VAI TRAZER ITENS DA TAREFA CLICADA OU SE FOI CRIADA UMA TAREFA NOVA -->*/
    private fun callApi() {
        if (mArgs.itemClickedMov1?.idTarefa.isNullOrEmpty()) {
            mBinding.apply {
                txtDoc.visibility = View.INVISIBLE
                txtSizeList.visibility = View.INVISIBLE
            }
            mViewModel.getTaskItemClick(mArgs.idNewTarefa!!.idTarefa)
        } else {
            mBinding.apply {
                txtDoc.visibility = View.VISIBLE
                txtSizeList.visibility = View.VISIBLE
            }
            mViewModel.getTaskItemClick(mArgs.itemClickedMov1!!.idTarefa)
        }
    }

    private fun setupSwipe() {
        val swipe = mBinding.swipeMov1
        swipe.setColorSchemeColors(requireActivity().getColor(R.color.color_default))
        swipe.setOnRefreshListener {
            setRecyclerView()
            callApi()
            swipe.isRefreshing = false
        }
    }


    /**ADICIONANDO NOVA TAREFA -->*/
    private fun initEditAddTask() {
        hideKeyExtensionFragment(mBinding.editMov2)
        mBinding.editMov2.addTextChangedListener { qrcode ->
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, visibility = true)
            if (qrcode.toString() != "") {
                if (mArgs.itemClickedMov1?.idTarefa.isNullOrEmpty()) {
                    mViewModel.addTask(
                        MovementAddTask(
                            mArgs.idNewTarefa!!.idTarefa,
                            qrcode.toString()
                        )
                    )
                    mBinding.editMov2.setText("")
                    mBinding.editMov2.requestFocus()
                } else {
                    mViewModel.addTask(
                        MovementAddTask(
                            mArgs.itemClickedMov1!!.idTarefa,
                            qrcode.toString()
                        )
                    )
                    mBinding.editMov2.setText("")
                    mBinding.editMov2.requestFocus()
                }
            }
        }
    }

    private fun setupObservable() {
        /**RESPOSTA MOSTRAR PROGRESSBAR -->*/
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { progressBar ->
            if (progressBar) {
                mProgress.show()
            } else {
                mProgress.hide()
            }
        }
        /**RESPOSTA DE ERRO AO TRAZER AS TAREFAS -->*/
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageErro ->
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageErro)
        }

        /**RESPOSTA MOSTRAR LINEAR NOMES TAREFAS-->*/
        mViewModel.mValidLinearShow.observe(viewLifecycleOwner) { validadLinear ->
            if (validadLinear) {
                mBinding.linearInfo.visibility = View.VISIBLE
            } else {
                mBinding.linearInfo.visibility = View.INVISIBLE
            }
        }

        /**RESPOSTA SUCESSO PARA CRIAR RECYCLERVIEW -->*/
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { list ->
            setTxtLinear(list)
            mAdapter.submitList(list)
            mProgress.hide()
        }

        /**RESPOSTA AO ADICIONAR TAREFAS -->*/
        mViewModel.mSucessAddTaskShow.observe(viewLifecycleOwner) {
            CustomMediaSonsMp3().somLeituraConcluida(requireContext())
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            callApi()
            setRecyclerView()
            mProgress.hide()
        }

        mViewModel.mErrorAddTaskShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            AppExtensions.visibilityProgressBar(mBinding.progressBarAddTarefa, false)
            CustomAlertDialogCustom().alertMessageErrorCancelFalse(requireContext(), messageError)
            mProgress.hide()
        }

        /**RESPOSTA AO FINALIZAR TAREFAS -->*/
        mViewModel.mSucessFinishShow.observe(viewLifecycleOwner) {
            CustomMediaSonsMp3().somSucess(requireContext())
            mBinding.buttonfinish.isEnabled = false
            callApi()
            setRecyclerView()
            mProgress.hide()
            CustomAlertDialogCustom().alertSucessFinishBack(
                requireActivity(),
                getString(R.string.finish_sucess)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTxtLinear(list: List<MovementReturnItemClickMov>?) {
        mBinding.apply {
            txtDoc.text = "Documento da Tarefa: ${mArgs.itemClickedMov1?.documento}"
            txtSizeList.text = "Total de volumes: ${list!!.size}"
        }
    }

    private fun clickButtonFinish() {
        mBinding.buttonfinish.setOnClickListener {
            CustomMediaSonsMp3().somAlerta(requireContext())
            alertFinish()
        }
    }

    /**
     * DIALOG QUE REALIDA A LEITURA PARA FINALIZAR A MOVIMENTAÇAO -->
     */
    private fun alertFinish() {
        val mAlert = AlertDialog.Builder(requireContext())
        val mBindingAlert =
            LayoutCustomFinishMovementAdressBinding.inflate(LayoutInflater.from(requireContext()))
        mAlert.setView(mBindingAlert.root)
        val mShow = mAlert.show()
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        hideKeyExtensionFragment(mBindingAlert.editQrcodeCustom)
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.editQrcodeCustom.addTextChangedListener { qrcode ->
            if (qrcode.toString() != "") {
                mBindingAlert.progressEdit.visibility = View.VISIBLE
                /**VALIDA SE O ID CHEGOU DO CLICK OU DE UMA NOVA TAREFA -->*/
                if (mArgs.itemClickedMov1?.idTarefa.isNullOrEmpty()) {
                    mBindingAlert.progressEdit.visibility = View.INVISIBLE
                    val idTarefa = mArgs.idNewTarefa!!.idTarefa
                    mViewModel.finishMovemet(
                        MovementFinishAndress(
                            idTarefa = idTarefa,
                            qrcode.toString()
                        )
                    )
                    mShow.dismiss()
                } else {
                    mBindingAlert.progressEdit.visibility = View.INVISIBLE
                    mViewModel.finishMovemet(
                        MovementFinishAndress(
                            mArgs.itemClickedMov1!!.idTarefa,
                            qrcode.toString()
                        )
                    )
                    mShow.dismiss()
                }
                mBindingAlert.editQrcodeCustom.setText("")
                mBindingAlert.editQrcodeCustom.requestFocus()
            }
        }

        mBindingAlert.buttonCancelCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            mShow.dismiss()
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgress.dismiss()
        _binding = null
    }

}