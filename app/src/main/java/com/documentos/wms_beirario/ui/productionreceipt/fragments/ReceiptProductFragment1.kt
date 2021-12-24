package com.documentos.wms_beirario.ui.productionreceipt.fragments

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.FragmentReceiptProduction1Binding
import com.documentos.wms_beirario.databinding.LayoutAlertdialogCustomFiltrarOperadorBinding
import com.documentos.wms_beirario.model.receiptproduct.PosLoginValidadREceipPorduct
import com.documentos.wms_beirario.model.receiptproduct.QrCodeReceipt1
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.ui.Tarefas.TipoTarefaActivity
import com.documentos.wms_beirario.ui.productionreceipt.adapters.AdapterReceiptProduct1
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel1
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class ReceiptProductFragment1 : Fragment() {

    private var mBinding: FragmentReceiptProduction1Binding? = null
    val binding get() = mBinding!!
    private val mService = ServiceApi.getInstance()
    private lateinit var mAdapter: AdapterReceiptProduct1
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mViewModel: ReceiptProductViewModel1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSharedPreferences = CustomSharedPreferences(requireContext())
        mViewModel = ViewModelProvider(
            this, ReceiptProductViewModel1.ReceiptProductFactory(
                ReceiptProductRepository(mService)
            )
        )[ReceiptProductViewModel1::class.java]
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
        setupEditQrCode()
        setupRecyclerView()
        setToolbar()
        getApi()
        setupObservables()
        return binding.root
    }

    private fun setupEditQrCode() {
        hideKeyExtensionFragment(mBinding!!.editRceipt1)
        mBinding!!.editRceipt1.addTextChangedListener { mQrCode ->
            if (mQrCode!!.isNotEmpty()) {
                mViewModel.postREadingQrCde(QrCodeReceipt1(codigoBarras = mQrCode.toString()))
                mBinding!!.editRceipt1.setText("")
                mBinding!!.editRceipt1.requestFocus()
            }
        }

    }

    private fun setupRecyclerView() {
        mAdapter = AdapterReceiptProduct1 { itemClick ->
            val action = ReceiptProductFragment1Directions.clickItemReceipt1(itemClick)
            findNavController().navAnimationCreate(action)
        }
        mBinding!!.recyclerViewReceipt.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun setToolbar() {
        mBinding!!.toolbar.setNavigationOnClickListener {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().extensionStarBacktActivity(TipoTarefaActivity())
            requireActivity().finish()
        }
    }

    private fun getApi() {
        val mIdOperador = mSharedPreferences.getString(CustomSharedPreferences.ID_OPERADOR)
        mViewModel.getReceipt1(true, mIdOperador.toString())
    }

    private fun setupObservables() {

        mViewModel.mValidaProgressReceiptShow.observe(viewLifecycleOwner) { validProgress ->
            if (validProgress) mBinding!!.progress.visibility = View.VISIBLE
            else mBinding!!.progress.visibility = View.INVISIBLE
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
            CustomMediaSonsMp3().somSucessReading(requireContext())
            getApi()
        }

        mViewModel.mErrorReceiptReadingShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
        }
    }

    /**--------------------OPTION MENU FILTRAR USUARIO------------------------------------------>*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_usuario_receipt, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_user -> {
                filterUser()
            }
        }
        return true
    }

    /**---------------------------ALERT DIALOG (FILTRAR POR OPERADOR)---------------------------->*/
    private fun filterUser() {
        CustomMediaSonsMp3().somClick(requireContext())
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val mBinding =
            LayoutAlertdialogCustomFiltrarOperadorBinding.inflate(LayoutInflater.from(requireContext()))
        mAlert.setCancelable(false)
        mAlert.setView(mBinding.root)
        val mShow = mAlert.show()
        mBinding.editUsuarioFiltrar.requestFocus()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        mBinding.buttonValidad.setOnClickListener {
            when {
                mBinding.editUsuarioFiltrar.text.toString().isEmpty() -> {
                    mBinding.editUsuarioFiltrar.error = "Ops! Digite o Usuario"
                }
                mBinding.editSenhaFiltrar.text.toString().isEmpty() -> {
                    mBinding.editSenhaFiltrar.error = "Ops! Digite a Senha!"
                }
                else -> {
                    mViewModel.postValidLoginAcesss(
                        PosLoginValidadREceipPorduct(
                            usuario = mBinding.editUsuarioFiltrar.text.toString(),
                            senha = mBinding.editSenhaFiltrar.text.toString()
                        )
                    )
                    mShow.dismiss()
                    mBinding.editUsuarioFiltrar.requestFocus()
                }
            }
            mSharedPreferences.saveString(
                CustomSharedPreferences.NOME_SUPERVISOR_LOGADO,
                mBinding.editUsuarioFiltrar.toString()
            )
        }

        mBinding.buttonClose.setOnClickListener {
            mShow.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}