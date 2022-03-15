package com.documentos.wms_beirario.ui.armazengem.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentArmazenagem01Binding
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.ui.TaskType.TipoTarefaActivity
import com.documentos.wms_beirario.ui.armazengem.adapter.ArmazenagemAdapter
import com.documentos.wms_beirario.ui.armazengem.viewmodel.ArmazenagemViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.example.coletorwms.constants.CustomMediaSonsMp3
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import org.koin.android.viewmodel.ext.android.viewModel

class ArmazenagemFragment1 : Fragment() {
    private var mBinding: FragmentArmazenagem01Binding? = null
    private val _binding get() = mBinding!!
    private val mViewModel: ArmazenagemViewModel by viewModel()
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
        initScan()
        setupObservables()
        setRecyclerView()
    }

    override fun onResume() {
        super.onResume()
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
                requireActivity().onBackTransitionExtension()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
        }
    }


    private fun initScan() {
        mBinding!!.editTxtArmazem01.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            val barcode = mBinding!!.editTxtArmazem01.text.toString()
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
                if (barcode.toString() != "") {
                    val qrcodeLido = mAdapter.procurarDestino(barcode.toString())
                    if (qrcodeLido == null) {
                        AppExtensions.visibilityProgressBar(
                            mBinding!!.progressBarEditArmazenagem1,
                            true
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            CustomAlertDialogCustom().alertMessageErrorCancelFalse(
                                requireContext(),
                                "Leia um endereço válido!"
                            )
                            AppExtensions.visibilityProgressBar(
                                mBinding!!.progressBarEditArmazenagem1,
                                false
                            )
                            setEdit()
                        }, 600)


                    } else {
                        AppExtensions.visibilityProgressBar(
                            mBinding!!.progressBarEditArmazenagem1,
                            visibility = false
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            CustomMediaSonsMp3().somSucess(requireContext())
                            abrirArmazem2(qrcodeLido)
                        }, 120)
                    }
                    setEdit()
                }
            }
            return@OnKeyListener false
        })
    }

    private fun setupObservables() {
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
            vibrateExtension(500)
            dialogError(message)
        })
        mViewModel.mValidProgress.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progressBarInitArmazenagem1.visibility = View.VISIBLE
            else
                mBinding!!.progressBarInitArmazenagem1.visibility = View.INVISIBLE
        }
    }

    private fun setEdit() {
        mBinding?.editTxtArmazem01?.setText("")
        mBinding?.editTxtArmazem01?.requestFocus()
    }

    private fun abrirArmazem2(qrcodeLido: ArmazenagemResponse) {
        val action = ArmazenagemFragment1Directions.actionArmazenagem01ToArmazenagemFragment02(
            itemConferidoArmazenagem = qrcodeLido
        )
        findNavController().navAnimationCreate(action)
    }

    private fun dialogError(message: String) {
        CustomMediaSonsMp3().somError(requireContext())
        val mAlert = AlertDialog.Builder(context)
        mAlert.setCancelable(false)
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.create()
        mShow.show()
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_error)
        medit.addTextChangedListener {
            if (it.toString() != "") {
                mShow.dismiss()
            }
        }
        val mText = inflate.findViewById<TextView>(R.id.txt_message_atencao)
        val mButton = inflate.findViewById<Button>(R.id.button_atencao_layout_custom)
        mText.text = message
        mButton.setOnClickListener {
            mShow.hide()
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
        }
        mAlert.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}