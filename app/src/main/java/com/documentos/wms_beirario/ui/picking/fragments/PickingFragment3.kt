package com.documentos.wms_beirario.ui.picking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentPicking3Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishMovementAdressBinding
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import com.documentos.wms_beirario.utils.extensions.navAnimationCreateback
import com.documentos.wms_beirario.model.picking.PickingRequest2
import com.documentos.wms_beirario.model.picking.PickingResponse3
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.ui.picking.adapters.AdapterPicking3
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel3
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3

class PickingFragment3 : Fragment() {

    private lateinit var mAdapter: AdapterPicking3
    private val mService = ServiceApi.getInstance()
    private var mBinding: FragmentPicking3Binding? = null
    val binding get() = mBinding!!
    private lateinit var mViewModel: PickingViewModel3
    private val mArgs: PickingFragment3Args by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPicking3Binding.inflate(layoutInflater)
        setupButtonsBacks()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(
            this, PickingViewModel3.PickingViewModelFactory3(
                PickingRepository(mService = mService)
            )
        )[PickingViewModel3::class.java]
        setupRecyclerView()
        setupObservablesReading()
    }

    private fun setupButtonsBacks() {
        mBinding!!.toolbarPicking3.setNavigationOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            val action = PickingFragment3Directions.clickBackToolbarPicking1()
            findNavController().navAnimationCreateback(action)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            CustomMediaSonsMp3().somClick(requireContext())
            val action = PickingFragment3Directions.clickBackToolbarPicking1()
            findNavController().navAnimationCreateback(action)
        }
        mBinding!!.buttonvoltarPicking3.setOnClickListener {
            CustomMediaSonsMp3().somClick(requireContext())
            val action = PickingFragment3Directions.clickButtonVoltar(mArgs.responseItemClick)
            findNavController().navAnimationCreateback(action)
        }

    }

    private fun setupRecyclerView() {
        mAdapter = AdapterPicking3 { itemClick ->
            alertFinishPicking(itemClick)

        }
        mBinding!!.rvPicking3.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        callApi()
    }

    private fun callApi() {
        mViewModel.getItensPicking()
        setupObservables()
    }

    private fun setupObservables() {
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { list ->
            mAdapter.submitList(list)
        }
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
        mViewModel.mValidProgressShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressBarAddPicking3.visibility = View.VISIBLE
            else
                mBinding!!.progressBarAddPicking3.visibility = View.INVISIBLE
        }
    }

    /**VALIDAR LEITURA !!!!!!!!!!!!!!!!!!!!!!!!!!!-->*/
    private fun alertFinishPicking(itemClick: PickingResponse3) {
        CustomMediaSonsMp3().somAlerta(requireContext())
        val mAlert = AlertDialog.Builder(requireContext())
        val mBindingAlert = LayoutCustomFinishMovementAdressBinding.inflate(layoutInflater)
        mAlert.setView(mBindingAlert.root)
        val mShow = mAlert.show()
        hideKeyExtensionFragment(mBindingAlert.editQrcodeCustom)
        mBindingAlert.editQrcodeCustom.requestFocus()
        mBindingAlert.txtInf.text =
            "Destino para: ${itemClick.descricaoEmbalagem} - ${itemClick.quantidade}"
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBindingAlert.progressEdit.visibility = View.INVISIBLE
        mBindingAlert.editQrcodeCustom.addTextChangedListener { qrcode ->
            if (qrcode.toString() != "") {
                mBindingAlert.progressEdit.visibility = View.VISIBLE
                mViewModel.finishTaskPicking(
                    PickingRequest2(
                        itemClick.idProduto,
                        itemClick.quantidade,
                        qrcode.toString()
                    )
                )
                mBindingAlert.editQrcodeCustom.setText("")
                mBindingAlert.editQrcodeCustom.requestFocus()
                mShow.dismiss()
            }
            mBindingAlert.progressEdit.visibility = View.INVISIBLE
        }
        mBindingAlert.buttonCancelCustom.setOnClickListener {
            mBindingAlert.progressEdit.visibility = View.INVISIBLE
            CustomMediaSonsMp3().somClick(requireContext())
            mShow.dismiss()
        }
    }

    private fun setupObservablesReading() {
        mViewModel.mSucessReadingShow.observe(viewLifecycleOwner) {
            CustomAlertDialogCustom().alertMessageSucess(
                requireContext(),
                getString(R.string.all_picking_sucess)
            )
            setupRecyclerView()
        }
        mViewModel.mErrorReadingShow.observe(viewLifecycleOwner) { messageErrorReading ->
            CustomAlertDialogCustom().alertMessageErrorSimples(
                requireContext(),
                messageErrorReading
            )
        }
    }

}

