package com.documentos.wms_beirario.ui.picking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentPicking2Binding
import com.documentos.wms_beirario.extensions.*
import com.documentos.wms_beirario.model.picking.PickingRequest1
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.AdapterPicking2
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class PickingFragment2 : Fragment() {

    private lateinit var mAdapter: AdapterPicking2
    private lateinit var mViewModel: PickingViewModel2
    private val mService = ServiceApi.getInstance()
    private var mBinding: FragmentPicking2Binding? = null
    val binding get() = mBinding!!
    private val mArgs: PickingFragment2Args by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(
            this,
            PickingViewModel2.PickingViewModelFactory2(PickingRepository(mService))
        )[PickingViewModel2::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPicking2Binding.inflate(layoutInflater)
        setupRecyclerView()
        setupToolbar()
        callApi()
        setupObservables()
        setupObservablesRead()
        readItem()
        clickButton()
        AppExtensions.visibilityProgressBar(mBinding!!.progressBarAddPicking2, false)
        return binding.root
    }

    private fun setupToolbar() {
        mBinding!!.toolbarPicking2.setNavigationOnClickListener {
            requireActivity().onBackTransition()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            val action = PickingFragment2Directions.clickBackAndroid()
            findNavController().navAnimationCreateback(action)
        }
    }


    private fun setupRecyclerView() {
        mAdapter = AdapterPicking2()
        mBinding!!.rvPicking2.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        callApi()
    }

    private fun callApi() {
        mViewModel.getItensPicking2(mArgs.responseItemClick!!.idArea)
    }

    private fun setupObservables() {
        /**Validar button enable/--/ler e recriar recyclerview*/
        mViewModel.mSucessPickingShow.observe(viewLifecycleOwner) { listSucess ->
            if (listSucess.isNotEmpty()) {
                mAdapter.submitList(listSucess)
                mBinding!!.buttonfinishpickin2.isEnabled = false

            } else {
                mBinding!!.buttonfinishpickin2.isEnabled = true
            }
        }

        mViewModel.mErrorPickingShow.observe(viewLifecycleOwner) { messageError ->
            CustomSnackBarCustom().snackBarErrorSimples(mBinding!!.root, messageError)
        }
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { progress ->
            if (progress) mBinding!!.progressBarInitPicking2.visibility = View.VISIBLE
            else
                mBinding!!.progressBarInitPicking2.visibility = View.INVISIBLE
        }
    }


    //-----------------------------------------READ------------------------------------------------>

    private fun readItem() {
        hideKeyExtension(mBinding!!.editPicking2)
        mBinding!!.editPicking2.addTextChangedListener { qrCode ->
            if (qrCode.toString() != "") {
                mViewModel.postPickingRead(
                    mArgs.responseItemClick!!.idArea,
                    PickingRequest1(qrCode.toString())
                )
                mBinding!!.editPicking2.setText("")
            }
        }
    }

    private fun setupObservablesRead() {
        mViewModel.mSucessPickingReadShow.observe(viewLifecycleOwner) {
            CustomMediaSonsMp3().somSucess(requireContext())
            setupRecyclerView()
        }
        mViewModel.mErrorReadingPickingShow.observe(viewLifecycleOwner) { messageError ->
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
    }

    private fun clickButton() {
        mBinding!!.buttonfinishpickin2.setOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            val action = PickingFragment2Directions.clickAvancar(mArgs.responseItemClick)
            findNavController().navAnimationCreate(action)
        }
    }
}
