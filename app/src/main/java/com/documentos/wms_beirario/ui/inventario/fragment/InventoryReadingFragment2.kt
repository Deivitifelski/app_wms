package com.documentos.wms_beirario.ui.inventario.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentInventoryReading2Binding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess
import com.documentos.wms_beirario.model.inventario.ResponseQrCode2
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterInventory2
import com.documentos.wms_beirario.ui.inventario.viewModel.InventoryReadingViewModel2
import com.example.coletorwms.constants.CustomSnackBarCustom
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


class InventoryReadingFragment2 : Fragment() {

    private lateinit var mAdapter: AdapterInventory2
    private var mBinding: FragmentInventoryReading2Binding? = null
    private val _binding get() = mBinding!!
    private var mRetrofit = RetrofitService.getInstance()
    private val mArgs: InventoryReadingFragment2Args by navArgs()
    private lateinit var mViewModel: InventoryReadingViewModel2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentInventoryReading2Binding.inflate(inflater, container, false)
        initRecyclerView()
        hideViews()
        setObservable()
        return _binding.root
    }

    private fun initRecyclerView() {
        mAdapter = AdapterInventory2()
        mBinding!!.rvLastReading.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(
            this, InventoryReadingViewModel2.InventoryReadingViewModelFactory(
                InventoryoRepository1(mRetrofitService = mRetrofit)
            )
        )[InventoryReadingViewModel2::class.java]
    }

    override fun onResume() {
        super.onResume()
        AppExtensions.visibilityProgressBar(mBinding!!.progressBar, false)
        setupEditQrcode()
    }

    private fun setupEditQrcode() {
        mBinding!!.editQrcode.requestFocus()
        mBinding!!.editQrcode.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            val barcode = mBinding!!.editQrcode.text.toString()
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP)  {
                if (barcode.isNotEmpty()) {
                    UIUtil.hideKeyboard(requireActivity())
                    mViewModel.readingQrCode(
                        inventoryReadingProcess = RequestInventoryReadingProcess(
                            mArgs.clickAdapter.id,
                            mArgs.clickAdapter.numeroContagem,
                            mArgs.clickAdapter.idArmazem,
                            codigoBarras = mBinding!!.editQrcode.text.toString()
                        )
                    )
                    mBinding!!.editQrcode.requestFocus()
                    mBinding!!.editQrcode.setText("")
                }
                return@OnKeyListener true
            }
            false
        })
    }

    private fun setObservable() {
        /**SUCESSO LEITURA -->*/
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { sucessReading ->
            setViews(sucessReading)
        }
        /**ERRO LEITURA -->*/
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
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
        if (diceReading.result.produtoPronto.isNullOrEmpty()) {
            mBinding!!.itTxtProdutos.text = "0"
        }else {
            mBinding!!.itTxtProdutos.text = diceReading.result.produtoPronto
        }
        mBinding!!.itTxtEndereco.text = diceReading.result.enderecoVisual
        mAdapter.submitList(diceReading.leituraEndereco)
    }

    private fun hideViews() {
        mBinding.apply {
            this!!.linearButton.visibility = View.INVISIBLE
            linearParent.visibility = View.INVISIBLE
        }
    }


}