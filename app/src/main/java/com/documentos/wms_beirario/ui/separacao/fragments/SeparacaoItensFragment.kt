package com.documentos.wms_beirario.ui.separacao.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentSeparacaoItensBinding
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity
import com.documentos.wms_beirario.ui.separacao.SeparacaoViewModel
import com.documentos.wms_beirario.utils.extensions.extensionStarBacktActivity
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel


class SeparacaoItensFragment : Fragment(), View.OnClickListener {

    private val TAG = "TESTE DE ITENS SEPARAÇAO -------->"
    private var mRetrofitService = ServiceApi.getInstance()
    private lateinit var mAdapter: AdapterSeparacaoItens
    private val mViewModel: SeparacaoViewModel by viewModel()
    private var mListstreets = mutableListOf<String>()
    private var binding: FragmentSeparacaoItensBinding? = null
    private val mBinding get() = binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeparacaoItensBinding.inflate(inflater, container, false)
        mBinding.lottie.visibility = View.INVISIBLE
        mBinding.buttonNext.setOnClickListener(this)
        setToolbar()
        initRv()
        setupObservables()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        setFragmentResultListener("back_result") { _, bundle ->
            val result = bundle.getSerializable("list_itens_check") as SeparationListCheckBox
            mAdapter.setCkeckBox(result.estantesCheckBox)
            for (element in result.estantesCheckBox) {
                Log.e(TAG, element)
            }
        }
        setupSelectAll()
        callApi()
        validateButton()
    }

    private fun setToolbar() {
        mBinding.buttonNext.isEnabled = false
        mBinding.toolbarSeparation.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
    }

    private fun setupSelectAll() {
        validateButton()
        if (mAdapter.mLIstEstantesCkeckBox.size > 4) {
            mBinding.checkboxSelectAll.visibility = View.VISIBLE
        } else {
            mBinding.checkboxSelectAll.visibility = View.INVISIBLE
        }
        mBinding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mAdapter.selectAll(mListstreets)
                mAdapter.setCkeckBox(mListstreets)
                mAdapter.notifyDataSetChanged()

            } else {
                mBinding.checkboxSelectAll.visibility = View.INVISIBLE
                initRv()
                mListstreets.clear()
                mAdapter.mLIstEstantesCkeckBox.clear()
                validateButton()
                callApi()
                setupObservables()
            }
        }
    }

    private fun initRv() {
        mAdapter = AdapterSeparacaoItens { _, itemscheckAdapter ->
            setupSelectAll()
            validateButton()
        }
        binding!!.rvSeparationItems.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
    }

    private fun validateButton() {
        mBinding.buttonNext.isEnabled =
            mAdapter.mLIstEstantesCkeckBox.isNotEmpty()
    }

    private fun callApi() {
        mViewModel.getItemsSeparation()
    }

    private fun setupObservables() {
        mViewModel.mValidaTxtShow.observe(requireActivity(), { validaTxt ->
            if (validaTxt) {
                mBinding.txtInf.visibility = View.VISIBLE
            } else {
                mBinding.txtInf.visibility = View.INVISIBLE
            }
        })
        mViewModel.mValidaProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding.progress.visibility = View.VISIBLE
            else mBinding.progress.visibility = View.INVISIBLE
        }
        mViewModel.mShowShow.observe(requireActivity(), { itensCheckBox ->
            if (itensCheckBox.isEmpty()) {
                mBinding.lottie.visibility = View.VISIBLE
            } else {
                mBinding.lottie.visibility = View.INVISIBLE
                itensCheckBox.map { list ->
                    mListstreets.add(list.estante)
                }
                setRecyclerView(itensCheckBox)
            }
        })

        mViewModel.mErrorShow.observe(requireActivity(), { message ->
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(binding!!.layoutParent, message)
        })
    }

    private fun setRecyclerView(itensCheckBox: List<ResponseItemsSeparationItem>) {
        mAdapter.submitList(itensCheckBox)
    }

    //CLICK BUTTON -->
    override fun onClick(button: View?) {
        when (button) {
            mBinding.buttonNext -> {
                val action =
                    SeparacaoItensFragmentDirections.actionSeparacaoItensFragmentToEndSeparationFragment(
                        SeparationListCheckBox(mAdapter.mLIstEstantesCkeckBox)
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mListstreets.clear()
        Log.e(TAG, mListstreets.toString())
    }
}