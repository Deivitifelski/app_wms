package com.documentos.wms_beirario.ui.separacao.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
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
    private lateinit var mShared: CustomSharedPreferences
    private var mIdArmazem: Int = 0
    private lateinit var mToken: String
    private var binding: FragmentSeparacaoItensBinding? = null
    private val mBinding get() = binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeparacaoItensBinding.inflate(inflater, container, false)
        mBinding.buttonNext.setOnClickListener(this)
        return mBinding.root

    }

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

        mShared = CustomSharedPreferences(requireContext())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        initRv()
        getShared()
        callApi()
        showresults()
    }

    private fun setToolbar() {
        mBinding.toolbar2.setNavigationOnClickListener {
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

    private fun showresults() {
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
    }

    private fun setRecyclerView(itensCheckBox: List<ResponseItemsSeparationItem>) {

        mAdapter.submitList(itensCheckBox)
    }

    private fun getShared() {
        mBinding.buttonNext.isEnabled = false
        mIdArmazem = mShared.getInt(CustomSharedPreferences.ID_ARMAZEM)
        mToken = mShared.getString(CustomSharedPreferences.TOKEN)!!
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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


}