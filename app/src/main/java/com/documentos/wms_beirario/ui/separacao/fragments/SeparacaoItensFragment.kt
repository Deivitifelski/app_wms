package com.documentos.wms_beirario.ui.separacao.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentSeparacaoItensBinding
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem
import com.documentos.wms_beirario.model.separation.SeparationListCheckBox
import com.documentos.wms_beirario.repository.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.SeparacaoViewModel
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class SeparacaoItensFragment : Fragment(), View.OnClickListener {

    private var mRetrofitService = RetrofitService.getInstance()
    private lateinit var mAdapter: AdapterSeparacaoItens
    private lateinit var mViewModel: SeparacaoViewModel
    private var binding: FragmentSeparacaoItensBinding? = null
    private val mBinding get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            SeparacaoViewModel.SeparacaoItensViewModelFactory(
                SeparacaoRepository(
                    mRetrofitService
                )
            )
        )[SeparacaoViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeparacaoItensBinding.inflate(inflater, container, false)
        mBinding.buttonNext.setOnClickListener(this)
        setToolbar()
        initRv()
        setupObservables()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        setFragmentResultListener("result") { key, bundle ->
            val result = bundle.getSerializable("bundle") as SeparationListCheckBox
            mAdapter.setCkeckBox(result.estantesCheckBox)
        }
        callApi()
        validateButton()
    }

    private fun setToolbar() {
        mBinding.buttonNext.isEnabled = false
        mBinding.toolbarSeparation.setNavigationOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            requireActivity().onBackTransition()
        }
    }

    private fun initRv() {
        mAdapter = AdapterSeparacaoItens { position, itemscheckAdapter ->
            validateButton()
        }
        binding!!.rvSeparationItems.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
    }

    private fun validateButton() {
        mBinding.buttonNext.isEnabled = mAdapter.mLIstEstantesCkeckBox.isNotEmpty()
    }

    private fun callApi() {
        mViewModel.getItemsSeparation()
    }

    private fun setupObservables() {
        mViewModel.mShowShow.observe(requireActivity(), { itensCheckBox ->
            setRecyclerView(itensCheckBox)
        })

        mViewModel.mErrorShow.observe(requireActivity(), { message ->
            CustomSnackBarCustom().snackBarPadraoSimplesBlack(binding!!.layoutParent, message)
        })

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
    }
}