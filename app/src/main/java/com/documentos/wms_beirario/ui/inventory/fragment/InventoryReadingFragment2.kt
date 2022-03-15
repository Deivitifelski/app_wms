package com.documentos.wms_beirario.ui.inventory.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentInventoryReading2Binding
import com.documentos.wms_beirario.databinding.LayoutAlertAtencaoOptionsBinding
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess
import com.documentos.wms_beirario.model.inventario.ResponseQrCode2
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventory2
import com.documentos.wms_beirario.ui.inventory.viewModel.InventoryReadingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.example.coletorwms.constants.CustomMediaSonsMp3
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import org.koin.android.viewmodel.ext.android.viewModel


class InventoryReadingFragment2 : Fragment() {

    private val mViewModel: InventoryReadingViewModel2 by viewModel()
    private lateinit var mAdapter: AdapterInventory2
    private var mBinding: FragmentInventoryReading2Binding? = null
    private val _binding get() = mBinding!!
    private val mArgs: InventoryReadingFragment2Args by navArgs()
    private lateinit var mProcess: RequestInventoryReadingProcess
    private var mIdEndereco: Int? = null
    private lateinit var mEnderecoVisual: String
    private lateinit var mData: ResponseQrCode2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInventoryReading2Binding.inflate(inflater, container, false)
        initRecyclerView()
        hideViews()
        setTollbar()
        setObservable()
        return _binding.root
    }

    private fun setTollbar() {
        mBinding!!.toolbar3.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
    }

    private fun initRecyclerView() {
        mAdapter = AdapterInventory2()
        mBinding!!.rvLastReading.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        AppExtensions.visibilityProgressBar(mBinding!!.progressBar, false)
        setupEditQrcode()

    }

    private fun setupEditQrcode() {
        hideKeyExtensionFragment(mBinding!!.editQrcode)
        mBinding!!.editQrcode.requestFocus()
        mBinding!!.editQrcode.addTextChangedListener { barcode ->
            if (barcode!!.isNotEmpty()) {
                UIUtil.hideKeyboard(requireActivity())
                /**CRIANDO O OBJETO A SER ENVIADO ->*/
                mProcess = RequestInventoryReadingProcess(
                    mArgs.clickAdapter.id,
                    numeroContagem = mArgs.clickAdapter.numeroContagem,
                    idEndereco = mIdEndereco,
                    codigoBarras = mBinding!!.editQrcode.text.toString()
                )

                /**ENVIANDO OBJETO  ->*/
                mViewModel.readingQrCode(
                    inventoryReadingProcess = mProcess
                )
                mBinding!!.editQrcode.setText("")
            }
        }
//        mBinding!!.editQrcode.requestFocus()
//        mBinding!!.editQrcode.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
//            val barcode = mBinding!!.editQrcode.text.toString()
//            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
//                if (barcode.isNotEmpty()) {
//                    UIUtil.hideKeyboard(requireActivity())
//                    /**CRIANDO O OBJETO A SER ENVIADO ->*/
//                    mProcess = RequestInventoryReadingProcess(
//                        mArgs.clickAdapter.id,
//                        numeroContagem = mArgs.clickAdapter.numeroContagem,
//                        idEndereco = mIdEndereco,
//                        codigoBarras = mBinding!!.editQrcode.text.toString()
//
//                    )
//                    /**ENVIANDO OBJETO  ->*/
//                    mViewModel.readingQrCode(
//                        inventoryReadingProcess = mProcess
//                    )
//                    mBinding!!.editQrcode.setText("")
//                }
//                return@OnKeyListener true
//            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                requireActivity().onBackTransitionExtension()
//            }
//            mBinding!!.editQrcode.requestFocus()
//        })
    }

    private fun setObservable() {
        /**SUCESSO LEITURA -->*/
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { response ->
            clickButton(response)
            if (mProcess.idEndereco != response.result.idEndereco && mProcess.idEndereco != null && response.result.idEndereco != 0) {
                mProcess.idEndereco = response.result.idEndereco
                alertDialog()
            } else {
                mData = response
                setViews(mData)
                if (response.result.idEndereco != 0) {
                    mProcess.idEndereco = response.result.idEndereco
                    mIdEndereco = response.result.idEndereco
                    mEnderecoVisual = response.result.enderecoVisual
                }
            }
        }
        /**ERRO LEITURA -->*/
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
        /**VALIDA PROGRESSBAR -->*/
        mViewModel.mValidaProgressShow.observe(viewLifecycleOwner) { validaProgress ->
            if (validaProgress) {
                mBinding!!.progressBar.visibility = View.VISIBLE
            } else {
                mBinding!!.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun setViews(diceReading: ResponseQrCode2) {
        mBinding!!.linearParent.visibility = View.VISIBLE
        mBinding!!.linearButton.visibility = View.VISIBLE
        mBinding!!.itTxtEndereco.text = diceReading.result.enderecoVisual
        mBinding!!.itTxtVolumes.text = diceReading.result.produtoVolume.toString()
        //PRODUTO PODE VIR NULL =(
        mBinding!!.itTxtEndereco.text = diceReading.result.enderecoVisual

        mBinding!!.itTxtVolumes.text = this.mData.result.produtoVolume.toString()
        if (this.mData.result.produtoPronto == null) {
            mBinding!!.itTxtProdutos.text = "0"
        } else {
            mBinding!!.itTxtProdutos.text = this.mData.result.produtoPronto.toString()
        }
        mBinding!!.itTxtEndereco.text = diceReading.result.enderecoVisual
        mAdapter.submitList(diceReading.leituraEnderecoCreateRvFrag2)
    }

    private fun hideViews() {
        mBinding.apply {
            this!!.linearButton.visibility = View.INVISIBLE
            linearParent.visibility = View.INVISIBLE
        }
    }


    private fun alertDialog() {
        vibrateExtension(500)
        CustomMediaSonsMp3().somError(requireContext())
        val mAlert = AlertDialog.Builder(requireContext())
        mAlert.setCancelable(false)
        val mBindinginto =
            LayoutAlertAtencaoOptionsBinding.inflate(LayoutInflater.from(requireContext()))
        mAlert.setView(mBindinginto.root)
        val mShow = mAlert.show()
        mBindinginto.txtMessageAtencao.text = getString(R.string.deseja_manter_endereço)
        mBindinginto.buttonSimAlert.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Cod.${mProcess.codigoBarras} mantido.",
                Toast.LENGTH_SHORT
            ).show()
            mShow.dismiss()
        }
        mBindinginto.buttonNaoAlert.setOnClickListener {
            mViewModel.readingQrCode(mProcess)
            mBinding!!.editQrcode.setText("")
            mShow.hide()
        }
    }

    private fun clickButton(responseQrCode: ResponseQrCode2) {
        mBinding!!.apply {
            buttonVerEnd.setOnClickListener {
                CustomMediaSonsMp3().somClick(requireContext())
                vibrateExtension(100)
                val action = InventoryReadingFragment2Directions.clickShowAndress(
                    responseQrCode,
                    mArgs.clickAdapter
                )
                findNavController().navAnimationCreate(action)
            }
            buttonAvulso.setOnClickListener {
                CustomMediaSonsMp3().somClick(requireContext())
                val action =
                    InventoryReadingFragment2Directions.clickCreateVoid(
                        responseQrCode = responseQrCode,
                        mArgs.clickAdapter
                    )
                findNavController().navAnimationCreate(action)

            }
        }
    }


}