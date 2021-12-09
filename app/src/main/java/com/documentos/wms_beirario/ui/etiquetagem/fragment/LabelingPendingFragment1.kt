package com.documentos.wms_beirario.ui.etiquetagem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.EtiquetagemFragment1FragmentBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.extensions.hideKeyExtension
import com.documentos.wms_beirario.extensions.navAnimationCreate
import com.documentos.wms_beirario.extensions.onBackTransition
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.EtiquetagemFragment1ViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3

class LabelingPendingFragment1 : Fragment() {
    private var mBinding: EtiquetagemFragment1FragmentBinding? = null
    private val mRetrofitService = RetrofitService.getInstance()
    val binding get() = mBinding!!
    private lateinit var mViewModel: EtiquetagemFragment1ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = EtiquetagemFragment1FragmentBinding.inflate(layoutInflater)
        AppExtensions.visibilityProgressBar(mBinding!!.progressBarEditEtiquetagem1, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel = ViewModelProvider(
            this, EtiquetagemFragment1ViewModel.EtiquetagemFactoryBarCode(
                EtiquetagemRepository(mRetrofitService)
            )
        )[EtiquetagemFragment1ViewModel::class.java]
        setupEdit()
        hideKeyExtension(mBinding!!.editEtiquetagem)
        clickButton()
        setToolbar()
    }

    private fun setToolbar() {
        mBinding!!.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransition()
            }
        }
    }

    /**ENVIANDO PARA OUTRO FRAGMENT -->*/
    private fun clickButton() {
        mBinding!!.buttonImagemEtiquetagem.setOnClickListener {
            CustomMediaSonsMp3().somAtencao(requireContext())
            val action = LabelingPendingFragment1Directions.clickButton()
            findNavController().navAnimationCreate(action)
        }
    }

    private fun setupEdit() {
        mBinding!!.editEtiquetagem.requestFocus()
        mBinding!!.editEtiquetagem.addTextChangedListener { qrcode ->
            if (qrcode!!.isNotEmpty()) {
                AppExtensions.visibilityProgressBar(mBinding!!.progressBarEditEtiquetagem1, true)
                mViewModel.etiquetagemPost(etiquetagemRequest1 = EtiquetagemRequest1(qrcode.toString()))
                mBinding!!.editEtiquetagem.setText("")
                mBinding!!.editEtiquetagem.requestFocus()
            }
        }
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            AppExtensions.visibilityProgressBar(mBinding!!.progressBarEditEtiquetagem1, false)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
    }


}