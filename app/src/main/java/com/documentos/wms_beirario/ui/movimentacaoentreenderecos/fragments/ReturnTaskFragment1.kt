package com.documentos.wms_beirario.ui.movimentacaoentreenderecos.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentReturnTask1Binding
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.adapter.Adapter1Movimentacao
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.ReturnTaskViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersion
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate


class ReturnTaskFragment1 : Fragment() {

    private lateinit var mAdapter: Adapter1Movimentacao
    private lateinit var mViewModel: ReturnTaskViewModel
    private lateinit var mShared: CustomSharedPreferences
    private var _binding: FragmentReturnTask1Binding? = null
    private val mBinding get() = _binding!!
    private lateinit var mProgress: Dialog
    private lateinit var mDialog: CustomAlertDialogCustom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReturnTask1Binding.inflate(inflater, container, false)
        initRv()
        initViewModel()
        setObservable()
        return mBinding.root
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this, ReturnTaskViewModel.Mov1ViewModelFactory(
                MovimentacaoEntreEnderecosRepository()
            )
        )[ReturnTaskViewModel::class.java]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDialog = CustomAlertDialogCustom()
        mProgress = CustomAlertDialogCustom().progress(
            requireContext(),
            getString(R.string.create_new_task)
        )
        mShared = CustomSharedPreferences(requireContext())
    }


    override fun onResume() {
        super.onResume()
        mProgress.hide()
        callApi()
        setToolbar()
        clickButtonNewTask()
    }

    private fun setToolbar() {
        mBinding.toolbarMov1.subtitle = "[${getVersion()}]"
        mBinding.toolbarMov1.apply {
            setNavigationOnClickListener {
                requireActivity().finish()
                requireActivity().extensionBackActivityanimation(requireContext())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
            requireActivity().extensionBackActivityanimation(requireContext())
        }
    }

    private fun initRv() {
        mAdapter = Adapter1Movimentacao { itemClicked ->
            CustomMediaSonsMp3().somClick(requireContext())
            val action =
                ReturnTaskFragment1Directions.actionReturnTaskFragment12ToEndMovementFragment2(
                    itemClicked, null
                )
            findNavController().navAnimationCreate(action)
        }

        mBinding.rvMovimentacao1.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setObservable() {
        //VALIDA O TEXTO SE A LISTA ESTA VAZIA -->
        mViewModel.mSucessEmplyShow.observe(viewLifecycleOwner) { txt ->
            if (txt) {
                mBinding.imageLottie.visibility = View.VISIBLE
                mBinding.txtListEmply.visibility = View.VISIBLE
            } else {
                mBinding.imageLottie.visibility = View.INVISIBLE
                mBinding.txtListEmply.visibility = View.INVISIBLE
            }
        }
        //DEFINE OS ITENS DA RECYCLERVIEW ->
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { listTask ->
            mAdapter.submitList(listTask)
        }
        //ERRO ->
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            mProgress.hide()
            mDialog.alertMessageErrorSimples(requireContext(), message = messageError)
        }
        //VALIDA PROGRESSBAR -->
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) {
                mBinding.progressBarInitMovimentacao1.visibility = View.VISIBLE
            } else {
                mBinding.progressBarInitMovimentacao1.visibility = View.INVISIBLE
            }
        }

        /** RESPOSTA DE NOVA TAREFA CRIADA COM SUCESSO -->*/
        mViewModel.mcreateNewTskShow.observe(viewLifecycleOwner) { newIdTask ->
            mProgress.hide()
            if (newIdTask.idTarefa.isNotEmpty()) {
                val action =
                    ReturnTaskFragment1Directions.actionReturnTaskFragment12ToEndMovementFragment2(
                        null, newIdTask
                    )
                findNavController().navAnimationCreate(action)
            }
        }
    }

    private fun clickButtonNewTask() {
        mBinding.buttonNewTask.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewModel.newTask()
            }, 1200)
            mProgress.show()
        }
    }

    private fun callApi() {
        mViewModel.returnTaskMov()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}