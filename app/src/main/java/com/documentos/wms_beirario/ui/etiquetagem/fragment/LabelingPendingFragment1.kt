package com.documentos.wms_beirario.ui.etiquetagem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.documentos.wms_beirario.databinding.EtiquetagemFragment1FragmentBinding
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemRequest1
import com.documentos.wms_beirario.repository.etiquetagem.EtiquetagemRepository
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection
import com.documentos.wms_beirario.ui.configuracoes.SetupNamePrinter
import com.documentos.wms_beirario.ui.etiquetagem.viewmodel.EtiquetagemFragment1ViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.extensionBackActivityanimation
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate


class LabelingPendingFragment1 : Fragment() {
    private var mBinding: EtiquetagemFragment1FragmentBinding? = null
    val binding get() = mBinding!!
    private lateinit var mViewModel: EtiquetagemFragment1ViewModel
    private lateinit var mAlert: CustomAlertDialogCustom
    private val TAG = "fragment.LabelingPendingFragment1"
    private lateinit var mPrintConnect: PrinterConnection





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = EtiquetagemFragment1FragmentBinding.inflate(layoutInflater)
        initViewModel()
        verificationsBluetooh()
        setObservable()
        setToolbar()
        setupEdit()
        hideKeyExtensionFragment(mBinding!!.editEtiquetagem)
        clickButton()
        AppExtensions.visibilityProgressBar(mBinding!!.progressBarEditEtiquetagem1, false)
        return binding.root
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            EtiquetagemFragment1ViewModel.Etiquetagem1ViewModelFactory(EtiquetagemRepository())
        )[EtiquetagemFragment1ViewModel::class.java]
    }

    /**VERIFICA SE JA TEM IMPRESSORA CONECTADA!!--->*/
    private fun verificationsBluetooh() {
        mAlert = CustomAlertDialogCustom()
        if (SetupNamePrinter.applicationPrinterAddress.isEmpty()) {
            mAlert.alertSelectPrinter(requireContext())
        }
    }

    private fun setToolbar() {
        mPrintConnect = PrinterConnection(SetupNamePrinter.applicationPrinterAddress)
        mBinding!!.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().finish()
                requireActivity().extensionBackActivityanimation(requireActivity())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
            requireActivity().extensionBackActivityanimation(requireActivity())
        }
    }

    /**ENVIANDO PARA OUTRO FRAGMENT -->*/
    private fun clickButton() {
        mBinding!!.buttonPendencePorNf.setOnClickListener {
            CustomMediaSonsMp3().somAtencao(requireContext())
            val action = LabelingPendingFragment1Directions.clickButton()
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.buttonPendencePorPendency.setOnClickListener {
            CustomMediaSonsMp3().somAtencao(requireContext())
            val action = LabelingPendingFragment1Directions.clickPendency()
            findNavController().navAnimationCreate(action)
        }
    }

    private fun setObservable(){
        mViewModel.mSucessShow.observe(viewLifecycleOwner, { zpl ->
            if (SetupNamePrinter.applicationPrinterAddress.isNullOrEmpty()) {
                mAlert.alertSelectPrinter(requireContext())
            } else {
                var mListZpl: MutableList<String> = mutableListOf()
                zpl.forEach {
                    mListZpl.add(it.codigoZpl)
                }
                mPrintConnect.printZebraLoop(mListZpl)
            }
        })

        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            mAlert.alertMessageAtencao(requireContext(), messageError)
        }
        mViewModel.mErrorAllShow.observe(viewLifecycleOwner, { errorAll ->
            mAlert.alertMessageErrorSimples(requireContext(), errorAll, 2000)
        })

        mViewModel.mProgressShow.observe(viewLifecycleOwner, { progress ->
            mBinding!!.progressBarEditEtiquetagem1.isVisible = progress
        })
    }

    private fun setupEdit() {
        mBinding!!.editEtiquetagem.requestFocus()
        mBinding!!.editEtiquetagem.addTextChangedListener { qrcode ->
            if (qrcode!!.isNotEmpty()) {
                mViewModel.etiquetagemPost(etiquetagemRequest1 = EtiquetagemRequest1(qrcode.toString()))
                clearEdit()
            }
        }

    }

    private fun clearEdit() {
        mBinding!!.editEtiquetagem.setText("")
        mBinding!!.editEtiquetagem.text?.clear()
        mBinding!!.editEtiquetagem.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}