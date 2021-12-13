package com.documentos.wms_beirario.ui.separacao.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentEndSeparationBinding
import com.documentos.wms_beirario.databinding.LayoutAlertSucessCustomBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.extensions.hideKeyExtension
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.extensions.vibrateExtension
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.SeparationEndViewModel
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparationEnd
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class EndSeparationFragment : Fragment() {

    private lateinit var mAdapter: AdapterSeparationEnd
    private lateinit var mViewModel: SeparationEndViewModel
    private val mRetrofitService = ServiceApi.getInstance()
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

        //FACTORY -->
        mViewModel = ViewModelProvider(
            this, SeparationEndViewModel.ViewModelEndFactory(
                SeparacaoRepository(mRetrofitService)
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
        onBack()
        hideKeyExtension(mBinding.editSeparacao2)
    }

    private fun setToolbar() {
        mBinding.toolbarSeparacao2.setNavigationOnClickListener {
            setFragmentResult("result", bundleOf("bundle" to mArgs.listCheck))
            requireActivity().onBackTransition()
        }
    }

    private fun initRecyclerView() {
        callApi()
        mAdapter = AdapterSeparationEnd()
        mBinding.rvSeparacaoEnd.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvSeparacaoEnd.adapter = mAdapter
    }

    private fun callApi() {
        mViewModel.postListCheck(mArgs.listCheck)
    }

    private fun initScanEditText() {
        mBinding.editSeparacao2.addTextChangedListener { mQrcode ->
            if (mQrcode.toString() != "") {
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
            initRecyclerView()
            showresultListCheck()
            if (mAdapter.getSize().isEmpty()) {
                alertMessageSucess(message = "$mQuantidade Volumes separados com sucesso! \n Aperte OK para voltar a tela anterior.")
            } else {
                CustomAlertDialogCustom().alertMessageSucess(
                    requireContext(),
                    "$mQuantidade Volumes separados com sucesso!"
                )
                initRecyclerView()
            }

        })
        mViewModel.mErrorSeparationEndShow.observe(this, { responseErrorEnd ->
            AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), responseErrorEnd)
        })
    }

    private fun alertMessageSucess(message: String) {
        CustomMediaSonsMp3().somSucess(requireContext())
        val mAlert = AlertDialog.Builder(requireContext())
        mAlert.setCancelable(false)
        val binding = LayoutAlertSucessCustomBinding.inflate(layoutInflater)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        mAlert.create()
        binding.editCustomAlertSucess.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        binding.txtMessageSucess.text = message
        binding.buttonSucessLayoutCustom.setOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            setFragmentResult("back_result", bundleOf("list_itens_check" to mArgs.listCheck))
            requireActivity().onBackTransition()
            mShow.dismiss()
        }
    }

    private fun onBack() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            setFragmentResult("back_result", bundleOf("list_itens_check" to mArgs.listCheck))
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}