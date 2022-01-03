package com.documentos.wms_beirario.ui.inventory.fragment.bottomNav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentBottomClickShowAndressBinding
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.repository.inventario.InventoryoRepository1
import com.documentos.wms_beirario.ui.inventory.viewModel.InventoryBarCodeFragmentButtonAndressViewModel
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.onBackTransitionExtension
import com.example.coletorwms.constants.CustomSnackBarCustom


class BottomClickShowAndressFragment : Fragment() {

    private lateinit var mViewModel: InventoryBarCodeFragmentButtonAndressViewModel
    private val mRetrofitService = ServiceApi.getInstance()
    private val mArgs: BottomClickShowAndressFragmentArgs by navArgs()
    private var mBindng: FragmentBottomClickShowAndressBinding? = null
    private val _binding get() = mBindng!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBindng = FragmentBottomClickShowAndressBinding.inflate(inflater, container, false)
        setToolbar()
        setupObservables()
        return _binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProvider(
            this,
            InventoryBarCodeFragmentButtonAndressViewModel.InventoryViewModelFactoryBarCode(
                InventoryoRepository1(mRetrofitService)
            )
        )[InventoryBarCodeFragmentButtonAndressViewModel::class.java]

    }

    override fun onResume() {
        super.onResume()
        callApi()


    }

    private fun setToolbar() {
        mBindng!!.toolbar.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
    }

    private fun callApi() {
        val idEndereco = mArgs.qrCode.result.idEndereco
        mViewModel.getItensRecyclerView(
            idEndereco = idEndereco,
            mArgs.responseClickInventory1.id,
            mArgs.responseClickInventory1.numeroContagem
        )
    }

    private fun setupObservables() {
        //SUCESS -->
        mViewModel.mSucessShowbuttonAndress.observe(viewLifecycleOwner) { responseSucess ->
            initListVol(responseSucess)
            clickBottomNav(responseSucess)
        }
        //ERROR -->
        mViewModel.mErrorShowButtonAndress.observe(viewLifecycleOwner) { errorMessage ->
            CustomSnackBarCustom().snackBarErrorSimples(mBindng!!.root, errorMessage)
        }
        //PROGRESS -->
        mViewModel.mValidaProgressShow.observe(viewLifecycleOwner) { validaProgress ->
            if (validaProgress) {
                AppExtensions.visibilityProgressBar(mBindng!!.initProgress, visibility = true)
            } else {
                AppExtensions.visibilityProgressBar(mBindng!!.initProgress, visibility = false)
            }
        }
    }

    private fun initListVol(responseSucess: ResponseListRecyclerView) {
        val bundle = bundleOf("VOLUME_SHOW_ANDRESS" to responseSucess)
        requireActivity().supportFragmentManager.commit {
            replace<VolumeBottomNavFragment>(
                R.id.fragment_parent_show_andress,
                args = bundle
            )
            setReorderingAllowed(true)
        }
    }

    private fun clickBottomNav(responseSucess: ResponseListRecyclerView) {
        mBindng!!.bottomNav.setOnItemSelectedListener { bottomNavClick ->
            when (bottomNavClick.itemId) {
                R.id.button_produto_nav -> {
                    val bundle = bundleOf(
                        "PRODUTO_SHOW_ANDRESS" to responseSucess,
                        "CONTEM_ID_INVENTORI" to mArgs.responseClickInventory1
                    )

                    requireActivity().supportFragmentManager.commit {
                        replace<ProdutoBottomNavFragment>(
                            R.id.fragment_parent_show_andress,
                            args = bundle
                        )
                        setReorderingAllowed(true)
                    }
                }
                R.id.button_volume_nav -> {
                    val bundle = bundleOf("VOLUME_SHOW_ANDRESS" to responseSucess)
                    requireActivity().supportFragmentManager.commit {
                        replace<VolumeBottomNavFragment>(
                            R.id.fragment_parent_show_andress,
                            args = bundle
                        )
                        setReorderingAllowed(true)
                    }
                }
            }
            true
        }
    }
}