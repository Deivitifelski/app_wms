package com.documentos.wms_beirario.ui.armazengem.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.documentos.wms_beirario.databinding.FragmentArmazenagem02Binding
import com.documentos.wms_beirario.model.armazenagem.ArmazemRequestFinish
import com.documentos.wms_beirario.ui.armazengem.viewmodel.ArmazenagemViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3
import org.koin.android.viewmodel.ext.android.viewModel

class ArmazenagemFragment2 : Fragment() {
    private var mBinding: FragmentArmazenagem02Binding? = null
    private val _binding get() = mBinding!!
    private val mViewModel: ArmazenagemViewModel by viewModel()
    private lateinit var mDialog: Dialog
    private val mArgs: ArmazenagemFragment2Args by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDialog = CustomAlertDialogCustom().progress(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArmazenagem02Binding.inflate(inflater, container, false)
        mDialog.hide()
        AppExtensions.visibilityProgressBar(mBinding!!.progressArmazenagemFinalizar, false)
        setToolbar()
        setTxt()
        setupReading()
        setObservables()
        return _binding.root
    }

    private fun setToolbar() {
        mBinding!!.toolbarArmazenagem2.apply {
            CustomMediaSonsMp3().somClick(requireContext())
            setNavigationOnClickListener {
                requireActivity().onBackTransitionExtension()
            }
        }
    }

    private fun setTxt() {
        mBinding!!.txtOrigemApi.text = mArgs.itemConferidoArmazenagem.visualEnderecoOrigem
        mBinding!!.txtDestinoApi.text = mArgs.itemConferidoArmazenagem.visualEnderecoDestino
    }

    private fun setupReading() {
        mBinding!!.editTxtArmazenagem02.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            val barcode = mBinding!!.editTxtArmazenagem02.text.toString()
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
                AppExtensions.visibilityProgressBar(mBinding!!.progressArmazenagemFinalizar, true)
                if (barcode.isNotEmpty()) {
                    mDialog.show()
                    mViewModel.postFinish(
                        ArmazemRequestFinish(
                            mArgs.itemConferidoArmazenagem.id,
                            barcode
                        )
                    )
                    mBinding!!.editTxtArmazenagem02.setText("")
                    mBinding!!.editTxtArmazenagem02.requestFocus()
                }
            }
            return@OnKeyListener false
        })
    }

    private fun setObservables() {
        mViewModel.mSucessFinishshow.observe(viewLifecycleOwner) {
            vibrateExtension(500)
            mDialog.hide()
            AppExtensions.visibilityProgressBar(mBinding!!.progressArmazenagemFinalizar, false)
            CustomAlertDialogCustom().alertSucessFinishBack(this, "Armazenado com sucesso!")
        }
        mViewModel.messageError.observe(viewLifecycleOwner) { message ->
            mDialog.hide()
            AppExtensions.visibilityProgressBar(mBinding!!.progressArmazenagemFinalizar, false)
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), message = message)
        }
    }

//    private fun alertSucessReading() {
//        vibrateExtension(500)
//        CustomMediaSonsMp3().somSucess(requireContext())
//        val alert = AlertDialog.Builder(requireContext())
//        val binding = LayoutAlertSucessCustomBinding.inflate(LayoutInflater.from(requireContext()))
//        alert.setView(binding.root)
//        val mAlert = alert.create()
//        mAlert.show()
//        mAlert.setCancelable(false)
//        binding.txtMessageSucess.text = getString(R.string.armazenado_sucess)
//        binding.buttonSucessLayoutCustom.setOnClickListener {
//            mAlert.dismiss()
//
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}