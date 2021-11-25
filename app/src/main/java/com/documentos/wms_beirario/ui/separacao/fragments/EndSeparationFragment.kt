package com.documentos.wms_beirario.ui.separacao.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentEndSeparationBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.model.separation.SeparationEnd
import com.documentos.wms_beirario.repository.SeparacaoRepository
import com.documentos.wms_beirario.ui.separacao.SeparationEndViewModel
import com.documentos.wms_beirario.ui.separacao.adapter.AdapterSeparationEnd
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class EndSeparationFragment : Fragment() {

    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mAdapter: AdapterSeparationEnd
    private var mIdArmazem: Int = 0
    private lateinit var mToken: String
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
        getShared()
        showresultListCheck()
        showresultEnd()
        initScanEditText()
    }

    private fun initRecyclerView() {
        callApi()
        mAdapter = AdapterSeparationEnd()
        mBinding.rvSeparacaoEnd.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvSeparacaoEnd.adapter = mAdapter
    }


    private fun initScanEditText() {
        mBinding.editSeparacao2.requestFocus()
        mBinding.editSeparacao2.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            val scan = mBinding.editSeparacao2.text.toString()
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
                if (scan != "") {
                    AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = true)
                    val qrcodeRead = mAdapter.searchSeparation(scan.toString())
                    if (qrcodeRead == null) {
                        CustomAlertDialogCustom().alertMessageErrorSimples(
                            requireContext(),
                            "Endereço inválido"
                        )
                    } else {
                        GlobalScope.launch(Dispatchers.Main) {
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
                    }
                    AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
                    mBinding.editSeparacao2.setText("")
                }
            }
            return@OnKeyListener true
        })
        false
    }


    /**MOSTRANDO ITENS A SEPARAR DOS ITENS SELECIONADOS DOS CHECK BOX --------------------------->*/
    private fun showresultListCheck() {
        mViewModel.mShowShow2.observe(this, { responseList ->
            mAdapter.update(responseList)
        })

        mViewModel.mErrorShow2.observe(this, { responseError ->
            CustomSnackBarCustom().snackBarSimplesBlack(mBinding.layoutSeparacao2, responseError)
        })

        mViewModel.mValidationProgressShow.observe(this, { showProgress ->
            if (!showProgress) {
                mBinding.progressSeparationInit.visibility = View.INVISIBLE
            } else {
                mBinding.progressSeparationInit.visibility = View.VISIBLE
            }
        })
    }

    /**LENDO EDIT TEXT PARA SEPARAR ------------------------------------------------------------->*/
    private fun showresultEnd() {
        mViewModel.mSeparationEndShow.observe(this, { responseUnit ->
            AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
            CustomAlertDialogCustom().alertMessageSucess(
                requireContext(),
                "$mQuantidade Volumes separados com sucesso!"
            )
            initRecyclerView()
        })
        mViewModel.mErrorSeparationEndShow.observe(this, { responseErrorEnd ->
            AppExtensions.visibilityProgressBar(mBinding.progressEdit, visibility = false)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), responseErrorEnd)
        })
    }

    private fun getShared() {
        mIdArmazem = mShared.getInt(CustomSharedPreferences.ID_ARMAZEM) ?: 0
        mToken = mShared.getString(CustomSharedPreferences.TOKEN)!!
    }

    @DelicateCoroutinesApi
    private fun callApi() {
        GlobalScope.launch(Dispatchers.Main) {
            mViewModel.postListCheck(mArgs.listCheck)
        }
    }


    private fun setToolbar() {
        mBinding.toolbarSeparacao2.setNavigationOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            findNavController().popBackStack()
            requireActivity().overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


}