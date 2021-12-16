package com.documentos.wms_beirario.ui.armazengem.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentArmazenagem01Binding
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import com.documentos.wms_beirario.utils.extensions.navAnimationCreate
import com.documentos.wms_beirario.utils.extensions.onBackTransition
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.repository.armazenagem.ArmazenagemRepository
import com.documentos.wms_beirario.ui.armazengem.adapter.ArmazenagemAdapter
import com.documentos.wms_beirario.ui.armazengem.viewmodel.ArmazenagemViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class ArmazenagemFragment_01 : Fragment() {
    private var mBinding: FragmentArmazenagem01Binding? = null
    private val _binding get() = mBinding!!
    private lateinit var mViewModel: ArmazenagemViewModel
    private var retrofitService = ServiceApi.getInstance()
    private lateinit var mAdapter: ArmazenagemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArmazenagem01Binding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UIUtil.hideKeyboard(requireActivity())
        setupToolbar()
        mViewModel =
            ViewModelProvider(
                this,
                ArmazenagemViewModel.ArmazenagemViewModelFactory(
                    ArmazenagemRepository(
                        retrofitService
                    )
                )
            )[ArmazenagemViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        initScan()
        initShared()
        setRecyclerView()
        AppExtensions.visibilityProgressBar(mBinding!!.progressBarEditArmazenagem1, false)
        mBinding!!.editTxtArmazem01.requestFocus()
        UIUtil.hideKeyboard(requireActivity())
    }

    private fun setRecyclerView() {
        mAdapter = ArmazenagemAdapter()
        mBinding?.rvArmazenagem?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setupToolbar() {
        mBinding!!.toolbarArmazenagem1.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransition()
            }
        }
    }


    private fun initShared() {
        mAdapter = ArmazenagemAdapter()
        mViewModel.getArmazenagem()

        mViewModel.mSucess.observe(requireActivity(), { listResponse ->
            if (listResponse.isEmpty()) {
                mBinding!!.imageLottieArmazenagem1.visibility = View.VISIBLE
            } else {
                mBinding!!.imageLottieArmazenagem1.visibility = View.INVISIBLE
                mAdapter.update(listResponse)
            }
        })

        mViewModel.messageError.observe(requireActivity(), { message ->
            CustomSnackBarCustom().snackBarErrorSimples(requireView(), message)
        })
        mViewModel.mValidProgress.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressBarInitArmazenagem1.visibility = View.VISIBLE
            else
                mBinding!!.progressBarInitArmazenagem1.visibility = View.INVISIBLE
        }
    }

    private fun initScan() {
        hideKeyExtensionFragment(mBinding!!.editTxtArmazem01)
        mBinding!!.editTxtArmazem01.addTextChangedListener { qrcode ->
            if (qrcode.toString() != "") {
                val qrcodeLido = mAdapter.procurarDestino(qrcode.toString())
                if (qrcodeLido == null) {
                    AppExtensions.visibilityProgressBar(
                        mBinding!!.progressBarEditArmazenagem1,
                        true
                    )
                    Handler().postDelayed({
                        CustomAlertDialogCustom().alertMessageErrorCancelFalse(
                            requireContext(),
                            "Leia um endereço válido!"
                        )
                        AppExtensions.visibilityProgressBar(
                            mBinding!!.progressBarEditArmazenagem1,
                            false
                        )
                    }, 600)


                } else {
                    AppExtensions.visibilityProgressBar(
                        mBinding!!.progressBarEditArmazenagem1,
                        visibility = false
                    )
                    Handler().postDelayed({
                        CustomMediaSonsMp3().somSucess(requireContext())
                        abrirArmazem2(qrcodeLido)
                    }, 120)
                }
                setEdit()
            }
        }
    }


    private fun setEdit() {
        mBinding?.editTxtArmazem01?.setText("")
        mBinding?.editTxtArmazem01?.requestFocus()
    }

    private fun abrirArmazem2(qrcodeLido: ArmazenagemResponse) {
        val action = ArmazenagemFragment_01Directions.actionArmazenagem01ToArmazenagemFragment02(
            qrcodeLido
        )
        findNavController().navAnimationCreate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}