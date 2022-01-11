package com.documentos.wms_beirario.ui.productionreceipt.fragments.filterSupervisor

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentFilterReceiptProduct3Binding
import com.documentos.wms_beirario.databinding.LayoutCustomFinishAndressBinding
import com.documentos.wms_beirario.model.receiptproduct.PostFinishReceiptProduct3
import com.documentos.wms_beirario.model.receiptproduct.ReceiptProduct2
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterReceiptProduct2
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.AppExtensions
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import com.documentos.wms_beirario.utils.extensions.navAnimationCreateback
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom
import org.koin.android.viewmodel.ext.android.viewModel


class FilterReceiptProductFragment3 : Fragment() {
    private var mBinding: FragmentFilterReceiptProduct3Binding? = null
    val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterReceiptProduct2
    private var mIdTarefa: String = ""
    private lateinit var mListItensValid: List<ReceiptProduct2>
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private val mViewModel: ReceiptProductViewModel2 by viewModel()
    private val mArgs: FilterReceiptProductFragment3Args by navArgs()
    private lateinit var mDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDialog = CustomAlertDialogCustom().progress(requireContext())
        mSharedPreferences = CustomSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFilterReceiptProduct3Binding.inflate(layoutInflater)
        mDialog.hide()
        clickButton()
        setRecyclerView()
        setupToolbar()
        callApi()
        setObservables()
        return binding.root
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

        mBinding!!.buttonBackFrag1Receipt.setOnClickListener {
            val action = FilterReceiptProductFragment3Directions.backScreenInit(true)
            findNavController().navAnimationCreateback(action)
        }
    }

    private fun callApi() {
        val idOperador = mSharedPreferences.getString(CustomSharedPreferences.ID_OPERADOR)
        mViewModel.getItem(
            idOperador = idOperador.toString(),
            filtrarOperario = true,
            pedido = mArgs.receiptProduct.pedido
        )
    }

    /**RETORNAR AO FRAGMENTO FILTER 2 COM OS ITENS PARA ELE SER RECRIADO -->*/
    private fun setupToolbar() {
        val getNameSupervisor =
            mSharedPreferences.getString(CustomSharedPreferences.NOME_SUPERVISOR_LOGADO)
        mBinding!!.toolbar2.subtitle = getString(R.string.supervisor_name, getNameSupervisor)
        mBinding!!.toolbar2.apply {
            this.setNavigationOnClickListener {
                val action = FilterReceiptProductFragment3Directions.clickOnBack2(
                    operatorSelect = mArgs.operadorSelect,
                    arrayOperatorPendences = mArgs.arrayOperadores
                )
                findNavController().navAnimationCreateback(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val action = FilterReceiptProductFragment3Directions.clickOnBack2(
                operatorSelect = mArgs.operadorSelect,
                arrayOperatorPendences = mArgs.arrayOperadores
            )
            findNavController().navAnimationCreateback(action)
        }
        mBinding!!.txtInf.text =
            getString(R.string.order_receipt2_toolbar, mArgs.receiptProduct.pedido)
    }

    private fun setObservables() {
        /**--------GET ITENS---------------->*/
        mViewModel.mSucessReceiptShow2.observe(viewLifecycleOwner) { listSucess ->
            if (listSucess.isEmpty()) {
                mBinding!!.buttonFinishReceipt2.isEnabled = false
            } else {
                mListItensValid = listSucess
                mIdTarefa = listSucess[0].idTarefa
                mAdapter.submitList(listSucess)
            }
        }
        mViewModel.mErrorReceiptShow2.observe(viewLifecycleOwner) { messageError ->
            mDialog.hide()
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
        mViewModel.mValidaProgressReceiptShow2.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) AppExtensions.visibilityProgressBar(mBinding!!.progress)
            else AppExtensions.visibilityProgressBar(mBinding!!.progress, visibility = false)
        }
        /**--------READING FINISH---------------->*/
        mViewModel.mSucessFinishShow.observe(viewLifecycleOwner) {
            CustomMediaSonsMp3().somSucessReading(requireContext())
            mDialog.hide()
            callApi()
            //Valida se todos itens forem armazenados o button fica inativo -->
            mBinding!!.buttonFinishReceipt2.isEnabled = mListItensValid.isNotEmpty()
            CustomSnackBarCustom().snackBarSucess(
                requireContext(),
                mBinding!!.root,
                "${mListItensValid.size} itens Armazenados!"
            )
        }
        mViewModel.mErrorFinishShow.observe(viewLifecycleOwner) { messageError ->
            mDialog.hide()
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
        mBinding.editQrcodeCustom.requestFocus()
        val showDialog = mAlert.create()
        showDialog.show()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBinding.editQrcodeCustom.addTextChangedListener { qrCode ->
            if (qrCode!!.isNotEmpty()) {
                mDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    mViewModel.postFinishReceipt(
                        PostFinishReceiptProduct3(
                            codigoBarrasEndereco = qrCode.toString(),
                            itens = AdapterReceiptProduct2.mListFinishReceiptProduct,
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
            mDialog.hide()
            showDialog.dismiss()
        }
    }

    /**-----------------------ALERT ERRO CUSTOMIZADO NO BUTTON OK-------------------------------->*/
    fun alertMessageErrorSimples(context: Context, message: String) {
        CustomMediaSonsMp3().somError(context)
        val mAlert = AlertDialog.Builder(context)
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.layout_alert_error_custom,null)
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
}
