package com.documentos.wms_beirario.ui.productionreceipt.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.CustomSharedPreferences.Companion.ID_OPERADOR
import com.documentos.wms_beirario.databinding.FragmentReceiptProduction1Binding
import com.documentos.wms_beirario.databinding.LayoutAlertdialogCustomFiltrarOperadorBinding
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterReceiptProduct1
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


class ReceiptProductFragment1 : Fragment() {

    private val TAG = "ReceiptProductFragment1"
    private var mBinding: FragmentReceiptProduction1Binding? = null
    val binding get() = mBinding!!
    private lateinit var mAdapter: AdapterReceiptProduct1
    private lateinit var mDialog: CustomAlertDialogCustom
    private lateinit var mShared: CustomSharedPreferences
    private lateinit var mViewModel: ReceiptProductViewModel1
    private var mValidaCallOperator: Boolean = false
    private val mArgs: ReceiptProductFragment1Args by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mShared = CustomSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentReceiptProduction1Binding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        AppExtensions.visibilityProgressBar(mBinding!!.progress)
        //Inflando toolbar in fragment -->
        (activity as AppCompatActivity?)!!.setSupportActionBar(mBinding!!.toolbar)
        visibilityLottieExtend(mBinding!!.imageLottie, false)
        setupEditQrCode()
        setupRecyclerView()
        setToolbar()
        setupObservables()
        getApi()
        setupReflesh()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        setupFilter()
    }

    private fun setupReflesh() {
        mBinding!!.reflesRecProd.apply {
            setOnRefreshListener {
                setColorSchemeColors(getColor(requireContext(), R.color.color_default))
                mBinding!!.progress.isVisible = true
                setupRecyclerView()
                getApi()
                mBinding!!.imageLottie.playAnimation()
                isRefreshing = false
            }
        }
    }

    private fun setupEditQrCode() {
        /**INIT VIEWMODEL -->*/
        mDialog = CustomAlertDialogCustom()
        mViewModel = ViewModelProvider(
            requireActivity(), ReceiptProductViewModel1.ReceiptProductViewModel1Factory(
                ReceiptProductRepository()
            )
        )[ReceiptProductViewModel1::class.java]

        UIUtil.hideKeyboard(requireActivity())
        /**LENDO EDIT TEXT -->*/
        mBinding!!.editRceipt1.setOnKeyListener { _, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
                Log.e(
                    TAG,
                    "setupObserver: CODIGO RECEBIDO ==== ${mBinding!!.editRceipt1.text.toString()}"
                )
                if (!mBinding!!.editRceipt1.text.toString().isNullOrEmpty()) {
                    mViewModel.postREadingQrCde(QrCodeReceipt1(codigoBarras = mBinding!!.editRceipt1.text.toString()))
                }
                clearEdit()
            }
            false
        }
    }

    private fun clearEdit() {
        mBinding!!.editRceipt1.setText("")
        mBinding!!.editRceipt1.requestFocus()
    }

    private fun setupRecyclerView() {
        /**VALIDA SE JA FOI FEITO LOGIN COMO SUPERVISOR PARA CONTINUAR LOGADO OU NAO --->*/
        mAdapter = AdapterReceiptProduct1 { itemClick ->
            if (mArgs.filterOperator || mValidaCallOperator) {
                val action =
                    ReceiptProductFragment1Directions.clickItemReceipt1(
                        itemClick,
                        true,
                    )
                findNavController().navAnimationCreate(action)

            } else {
                val action = ReceiptProductFragment1Directions.clickItemReceipt1(
                    itemClick,
                    false
                )
                findNavController().navAnimationCreate(action)
            }
        }
        mBinding!!.recyclerViewReceipt.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setToolbar() {
        mBinding!!.toolbar.subtitle = requireActivity().getVersionNameToolbar()
        mBinding!!.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
            requireActivity().extensionBackActivityanimation(requireContext())
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
            requireActivity().extensionBackActivityanimation(requireContext())
        }
    }

    /**PRIMEIRA CHAMADA API TRAS PENDENCIAS DO USUARIO LOGADO -->*/
    private fun getApi() {
        val mIdOperador = mShared.getString(ID_OPERADOR)
        mViewModel.getReceipt1(filtrarOperador = true, mIdOperador = mIdOperador ?: "0")
    }

    private fun setupObservables() {
        mViewModel.mValidaProgressReceiptShow.observe(viewLifecycleOwner) { validProgress ->
            mBinding!!.progress.isVisible = validProgress
        }

        mViewModel.mSucessReceiptShow.observe(viewLifecycleOwner) { listReceipt ->
            //CASO VAZIA -->
            if (listReceipt.isEmpty()) {
                mBinding!!.txtInf.text = getText(R.string.list_emply)
                visibilityLottieExtend(mBinding!!.imageLottie)
            } else {
                mAdapter.submitList(listReceipt)
                mBinding!!.txtInf.text = getString(R.string.click_store_order)
                visibilityLottieExtend(mBinding!!.imageLottie, false)
            }
        }
        mViewModel.mErrorReceiptShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomSnackBarCustom().snackBarErrorAction(mBinding!!.root, messageError)
        }
        /**---READING--->*/
        mViewModel.mSucessReceiptReadingShow.observe(viewLifecycleOwner) {
            clearEdit()
            CustomMediaSonsMp3().somSucessReading(requireContext())
            getApi()
        }

        mViewModel.mErrorReceiptReadingShow.observe(viewLifecycleOwner) { messageError ->
            clearEdit()
            mDialog.alertMessageErrorSimples(requireContext(), messageError, 2000)
        }
        /**---VALIDAD LOGIN ACESSO--->*/
        mViewModel.mSucessReceiptValidLoginShow.observe(viewLifecycleOwner) {
            UIUtil.hideKeyboard(requireActivity())
            /**CASO SUCESSO IRA ALTERAR O ICONE E VALIDAR SEM PRECISAR EFETUAR O LOGIN NOVAMENTE--->*/
            mViewModel.callPendenciesOperator()
        }

        /**---VALIDA CHAMADA QUE TRAS OPERADORES COM PENDENCIAS OU SEJA,NO CLICK DO MENU --->*/
        mViewModel.mSucessGetPendenceOperatorShow.observe(viewLifecycleOwner) { listOpPendentes ->
            try {
                val idOpCorrent = mShared.getString(ID_OPERADOR).toString()
                val listSemUserCorrent =
                    listOpPendentes.filter { it.idOperadorColetor.toString() != idOpCorrent }
                when {
                    /**CASO 1 -> LISTA VAZIA */
                    listOpPendentes.isEmpty() -> {
                        vibrateExtension(500)
                        mDialog.alertMessageAtencao(
                            requireContext(),
                            getString(R.string.not_operator_pendenc), 2000
                        )
                    }
                    /**CASO 2 -> LISTA TENHA APENAS UM OPERADOR E FOR IGUAL AO USUARIO */
                    listOpPendentes.size == 1 && listOpPendentes[0].idOperadorColetor.toString() == idOpCorrent -> {
                        vibrateExtension(500)
                        mDialog.alertMessageAtencao(
                            requireContext(),
                            getString(R.string.not_operator_pendenc), 2000
                        )
                    }
                    /**CASO 3 -> VARIOS OPERADORES ENTAO PRECISO EXCLUIR O DO PROPIO USER --> */
                    else -> {
                        val action = ReceiptProductFragment1Directions.clickMenuOperator(
                            true,
                            listSemUserCorrent.toTypedArray()
                        )
                        findNavController().navAnimationCreate(action)

                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "setupObservables: $e")
                Toast.makeText(
                    requireContext(),
                    "Erro ao receber lista operadores com pendências!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mViewModel.mErrorGetPendenceOperatorShow.observe(viewLifecycleOwner) { errorOperador ->
            Toast.makeText(requireContext(), errorOperador, Toast.LENGTH_SHORT).show()
        }
    }

    /**VERIFICA SE LOGIN FOI FEITO ENTAO ALTERA DRAWABLE E CONTINUA LOGADO --->*/
    private fun setupFilter() {
        mBinding!!.editRceipt1.requestFocus()
        UIUtil.hideKeyboard(requireActivity(), mBinding!!.editRceipt1)
    }


    /**--------------------OPTION MENU FILTRAR USUARIO------------------------------------------>*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_usuario_receipt, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_user -> {
                if (!mValidaCallOperator) {
                    filterUser()
                } else {
                    mViewModel.callPendenciesOperator()
                }
            }
        }
        return true
    }

    /**---------------------------ALERT DIALOG (FILTRAR POR OPERADOR)---------------------------->*/
    private fun filterUser() {
        CustomMediaSonsMp3().somAtencao(requireContext())
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val binding =
            LayoutAlertdialogCustomFiltrarOperadorBinding.inflate(LayoutInflater.from(requireContext()))
        mAlert.setCancelable(false)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        binding.editUsuarioFiltrar.requestFocus()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        binding.buttonValidad.setOnClickListener {
            when {
                binding.editUsuarioFiltrar.text.toString().isEmpty() -> {
                    binding.editUsuarioFiltrar.error = "Ops! Digite o Usuario"
                }
                binding.editSenhaFiltrar.text.toString().isEmpty() -> {
                    binding.editSenhaFiltrar.error = "Ops! Digite a Senha!"
                }
                else -> {
                    mViewModel.postValidLoginAcesss(
                        PosLoginValidadREceipPorduct(
                            usuario = binding.editUsuarioFiltrar.text.toString(),
                            senha = binding.editSenhaFiltrar.text.toString()
                        )
                    )
                    mShow.dismiss()
                    binding.editUsuarioFiltrar.requestFocus()
                }
            }
            mShared.saveString(
                CustomSharedPreferences.NOME_SUPERVISOR_LOGADO,
                binding.editUsuarioFiltrar.text.toString()
            )
        }

        binding.buttonClose.setOnClickListener {
            mShow.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}
