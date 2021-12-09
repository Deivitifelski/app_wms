package com.documentos.wms_beirario.ui.separacao.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentEndSeparationBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.extensions.hideKeyExtension
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.extensions.vibrateExtension
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.repository.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.SeparationEndViewModel
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparationEnd
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomSnackBarCustom
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class EndSeparationFragment : Fragment() {

    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mAdapter: AdapterSeparationEnd
    private lateinit var mViewModel: SeparationEndViewModel
    private val mRetrofitService = RetrofitService.getInstance()
    private var mQuantidade: Int = 0
    private var _binding: FragmentEndSeparationBinding? = null
    private val mBinding get() = _binding!!
    private val mArgs: EndSeparationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEndSeparationBinding.inflate(inflater, container, false)
        setToolbar()
        return mBinding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mShared = CustomSharedPreferences(requireContext())
        //FACTORY -->
        mViewModel = ViewModelProvider(
            this, SeparationEndViewModel.ViewModelEndFactory(
                SeparacaoRepository(mRetrofitService = mRetrofitService)
            )
        )[SeparationEndViewModel::class.java]
    }


    override fun onResume() {
        super.onResume()
        UIUtil.hideKeyboard(requireActivity())
        AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
        initRecyclerView()
        showresultListCheck()
        showresultEnd()
        initScanEditText()
        hideKeyExtension(mBinding.editSeparacao2)
    }


    private fun initRecyclerView() {
        callApi()
        mAdapter = AdapterSeparationEnd()
        mBinding.rvSeparacaoEnd.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvSeparacaoEnd.adapter = mAdapter
    }

    private fun initScanEditText() {
        mBinding.editSeparacao2.addTextChangedListener { mQrcode ->
            if (mQrcode.toString() != "") {
                AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = true)
                val qrcodeRead = mAdapter.searchSeparation(mQrcode.toString())
                if (qrcodeRead == null) {
                    CustomAlertDialogCustom().alertMessageErrorSimples(
                        requireContext(),
                        "Endereço inválido"
                    )
                } else {
                    mQuantidade = qrcodeRead.quantidadeSeparar
                    mViewModel.postSeparationEnd(
                        SeparationEnd(
                            qrcodeRead.idEnderecoOrigem,
                            qrcodeRead.idEnderecoDestino,
                            qrcodeRead.idProduto,
                            qrcodeRead.quantidadeSeparar
                        )
                    )
                }
                AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
                mBinding.editSeparacao2.setText("")
                mBinding.editSeparacao2.requestFocus()
            }
        }
    }


    /**MOSTRANDO ITENS A SEPARAR DOS ITENS SELECIONADOS DOS CHECK BOX --------------------------->*/
    private fun showresultListCheck() {
        mViewModel.mShowShow2.observe(this, { responseList ->
            mAdapter.update(responseList)
        })

        mViewModel.mErrorShow2.observe(this, { responseError ->
            vibrateExtension(500)
            CustomSnackBarCustom().snackBarSimplesBlack(mBinding.layoutSeparacao2, responseError)
        })

        mViewModel.mValidationProgressShow.observe(viewLifecycleOwner) { showProgress ->
            if (showProgress) {
                mBinding.progressSeparationInit.visibility = View.VISIBLE
            } else {
                mBinding.progressSeparationInit.visibility = View.INVISIBLE
            }
        }
    }

    /**LENDO EDIT TEXT PARA SEPARAR ------------------------------------------------------------->*/
    private fun showresultEnd() {
        mViewModel.mSeparationEndShow.observe(this, { responseUnit ->
            vibrateExtension(500)
            AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
            CustomAlertDialogCustom().alertMessageSucess(
                requireContext(),
                "$mQuantidade Volumes separados com sucesso!"
            )
            initRecyclerView()
            setFragmentResult("result", bundleOf("bundle" to mArgs.listCheck))
        })
        mViewModel.mErrorSeparationEndShow.observe(this, { responseErrorEnd ->
            AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), responseErrorEnd)
        })
    }


    private fun callApi() {
        mViewModel.postListCheck(mArgs.listCheck)

    }


    private fun setToolbar() {
        mBinding.toolbarSeparacao2.setNavigationOnClickListener {
            setFragmentResult("result", bundleOf("bundle" to mArgs.listCheck))
            requireActivity().onBackTransition()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}