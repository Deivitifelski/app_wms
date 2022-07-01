package com.documentos.wms_beirario.ui.productionreceipt.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.LayoutCustomFinishAndressBinding
import com.documentos.wms_beirario.databinding.ReceiptProductFragment2Binding
import com.documentos.wms_beirario.model.receiptproduct.ListFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.PostFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct2
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterReceiptProduct2
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import com.documentos.wms_beirario.utils.extensions.navAnimationCreateback
import com.documentos.wms_beirario.utils.extensions.vibrateExtension

class ReceiptProductFragment2 : Fragment(R.layout.receipt_product_fragment2) {

    private var mBinding: ReceiptProductFragment2Binding? = null
    val binding get() = mBinding!!
    val TAG = "ReceiptProductFragment2"
    private lateinit var mAdapter: AdapterReceiptProduct2
    private var mIdTarefa: String = ""
    private lateinit var mListItensValid: List<ReceiptProduct2>
    private var mListItensFinish = mutableListOf<ListFinishReceiptProduct3>()
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mViewModel: ReceiptProductViewModel2
    private val mArgs: ReceiptProductFragment2Args by navArgs()
    private lateinit var mProgress: Dialog
    private lateinit var mDialog: CustomAlertDialogCustom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = ReceiptProductFragment2Binding.inflate(layoutInflater)
        clickButton()
        initConst()
        setRecyclerView()
        setupToolbar()
        callApi()
        setObservables()
        return binding.root
    }

    private fun initConst() {
        mViewModel = ViewModelProvider(
            requireActivity(), ReceiptProductViewModel2.ReceiptProductViewModel1Factory2(
                ReceiptProductRepository()
            )
        )[ReceiptProductViewModel2::class.java]

        mDialog = CustomAlertDialogCustom()
        mProgress = CustomAlertDialogCustom().progress(requireContext())
        mSharedPreferences = CustomSharedPreferences(requireContext())
    }

    override fun onResume() {
        super.onResume()
        mProgress.hide()
    }


    private fun setRecyclerView() {
        mAdapter = AdapterReceiptProduct2()
        mBinding!!.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun clickButton() {
        mBinding!!.buttonFinishReceipt2.setOnClickListener {
            alertArmazenar()
        }
    }


    private fun callApi() {
        val idOperador =
            mSharedPreferences.getString(CustomSharedPreferences.ID_OPERADOR)
        Log.e(TAG, "callApi --> ${mArgs.responseClickPendence.pedido} + $idOperador")
        mViewModel.getItem(
            idOperador = idOperador ?: "0",
            filtrarOperario = false,
            pedido = mArgs.responseClickPendence.pedido
        )

    }

    /**VALIDA SE O USUARIO FOI LOGADO RETORNA TRUE OU FALSE NO ARGUMENTO -->*/
    private fun setupToolbar() {
        mBinding!!.toolbar2.apply {
            this.setNavigationOnClickListener {
                val action = ReceiptProductFragment2Directions.backFrag1(
                    filterOperator = mArgs.validadLoginSupervisor
                )
                findNavController().navAnimationCreateback(action)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val action = ReceiptProductFragment2Directions.backFrag1(mArgs.validadLoginSupervisor)
            findNavController().navAnimationCreateback(action)
        }
        mBinding!!.txtInf.text =
            getString(R.string.order_receipt2_toolbar, mArgs.responseClickPendence.pedido)
    }

    private fun setObservables() {
        /**GET ITENS / VALIDA SE A LISTA FOR VAZIA BUTTON FINISH INATIVO-->*/
        mViewModel.mSucessReceiptShow2.observe(viewLifecycleOwner) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding!!.buttonFinishReceipt2.isEnabled = false
                mBinding!!.txtInf.isVisible = true
            } else {
                mBinding!!.txtInf.isVisible = false
                listSucess.forEach { itens ->
                    mListItensFinish.add(
                        ListFinishReceiptProduct3(
                            itens.numeroSerie,
                            itens.sequencial
                        )
                    )
                }
                mListItensValid = listSucess
                mIdTarefa = listSucess[0].idTarefa
                mAdapter.submitList(listSucess)
            }
        }
        mViewModel.mErrorReceiptShow2.observe(viewLifecycleOwner) { messageError ->
            mProgress.hide()
            vibrateExtension(500)
            mBinding!!.txtInf.isVisible = true
            mBinding!!.txtInf.text = messageError
            mDialog.alertMessageErrorSimples(requireContext(), messageError, 2000)
        }
        mViewModel.mValidaProgressReceiptShow2.observe(viewLifecycleOwner) { validProgress ->
            mBinding!!.progress.isVisible = validProgress
        }
        /**--------READING FINISH---------------->*/
        mViewModel.mSucessFinishShow.observe(viewLifecycleOwner) {
            CustomMediaSonsMp3().somSucess(requireContext())
            mProgress.hide()
            callApi()
            setRecyclerView()
            //Valida se todos itens forem armazenados o button fica inativo -->
            mBinding!!.buttonFinishReceipt2.isEnabled = mListItensValid.isNotEmpty()
            CustomSnackBarCustom().snackBarSucess(
                requireContext(),
                mBinding!!.root,
                "${mListItensValid.size} itens finalizados!"
            )
            Handler(Looper.getMainLooper()).postDelayed({
                val action =
                    ReceiptProductFragment2Directions.backFrag1(mArgs.validadLoginSupervisor)
                findNavController().navAnimationCreateback(action)
            }, 1500)


        }
        mViewModel.mErrorFinishShow.observe(viewLifecycleOwner) { messageError ->
            mProgress.hide()
            vibrateExtension(500)
            alertMessageErrorSimples(requireContext(), messageError)
        }
    }

    /**-----------------------ALERT CAIXA PARA FINALIZAR LEITURA ENDEREÇO------------------------>*/
    private fun alertArmazenar() {
        AppExtensions.vibrar(requireContext())
        CustomMediaSonsMp3().somAtencao(requireContext())
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val mBinding =
            LayoutCustomFinishAndressBinding.inflate(LayoutInflater.from(requireContext()))
        mAlert.setCancelable(false)
        mAlert.setView(mBinding.root)
        hideKeyExtensionFragment(mBinding.editQrcodeCustom)
        mBinding.txtCustomAlert.text = "Área destino: ${mArgs.responseClickPendence.areaDestino}"
        mBinding.editQrcodeCustom.requestFocus()
        val showDialog = mAlert.create()
        showDialog.show()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBinding.editQrcodeCustom.addTextChangedListener { qrCode ->
            if (qrCode!!.isNotEmpty()) {
                mProgress.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    mViewModel.postFinishReceipt(
                        PostFinishReceiptProduct3(
                            codigoBarrasEndereco = qrCode.toString(),
                            itens = mListItensFinish,
                            idTarefa = mIdTarefa
                        )
                    )
                }, 600)
                showDialog.dismiss()

                mBinding.editQrcodeCustom.setText("")
                mBinding.editQrcodeCustom.requestFocus()
            }
        }
        mBinding.buttonCancelCustom.setOnClickListener {
            mProgress.hide()
            showDialog.dismiss()
        }
    }

    /**-----------------------ALERT ERRO CUSTOMIZADO NO BUTTON OK-------------------------------->*/
    fun alertMessageErrorSimples(context: Context, message: String) {
        CustomMediaSonsMp3().somError(context)
        val mAlert = AlertDialog.Builder(context)
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom, null)
        mAlert.apply {
            setView(inflate)
        }
        val mShow = mAlert.show()
        val medit = inflate.findViewById<EditText>(R.id.edit_custom_alert_error)
        medit.addTextChangedListener {
            if (it.toString() != "") {
                alertArmazenar()
                mShow.dismiss()
            }
        }
        val mText = inflate.findViewById<TextView>(R.id.txt_message_atencao)
        val mButton = inflate.findViewById<Button>(R.id.button_atencao_layout_custom)
        mText.text = message
        mButton.setOnClickListener {
            alertArmazenar()
            mShow.dismiss()
        }
        mAlert.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        mProgress.dismiss()
    }

}