package com.documentos.wms_beirario.ui.inventory.fragment.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentInventoryReading2Binding
import com.documentos.wms_beirario.databinding.LayoutAlertAtencaoOptionsBinding
import com.documentos.wms_beirario.model.inventario.LeituraEndInventario2List
import com.documentos.wms_beirario.model.inventario.ProcessaLeituraResponseInventario2
import com.documentos.wms_beirario.model.inventario.RequestInventoryReadingProcess
import com.documentos.wms_beirario.ui.inventory.adapter.AdapterInventory2
import com.documentos.wms_beirario.ui.inventory.viewModel.InventoryReadingViewModel2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.*
import com.example.coletorwms.constants.CustomMediaSonsMp3
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import org.koin.android.viewmodel.ext.android.viewModel


class InventoryReadingFragment2 : Fragment() {

    private val mViewModel: InventoryReadingViewModel2 by viewModel()
    private lateinit var mAdapter: AdapterInventory2
    private var mBinding: FragmentInventoryReading2Binding? = null
    private val _binding get() = mBinding!!
    private val mArgs: InventoryReadingFragment2Args by navArgs()
    private lateinit var mProcess: RequestInventoryReadingProcess
    private var mIdAndress: Int? = null
    private lateinit var mNewResultObj: ProcessaLeituraResponseInventario2
    private lateinit var mNewResultObjIfNull: ProcessaLeituraResponseInventario2
    private lateinit var mNewResultObjIfChange: ProcessaLeituraResponseInventario2


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservable()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInventoryReading2Binding.inflate(inflater, container, false)
        initRecyclerView()
        hideViews()
        setTollbar()
        setupEditQrcode()
        return _binding.root
    }

    private fun setTollbar() {
        mBinding!!.toolbar3.setNavigationOnClickListener {
            requireActivity().onBackTransitionExtension()
        }
    }

    private fun initRecyclerView() {
        mAdapter = AdapterInventory2()
        mBinding!!.rvLastReading.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        AppExtensions.visibilityProgressBar(mBinding!!.progressBar, false)

    }

    private fun setupEditQrcode() {
        hideKeyExtensionFragment(mBinding!!.editQrcode)
        mBinding!!.editQrcode.requestFocus()
        mBinding!!.editQrcode.addTextChangedListener { barcode ->
            if (barcode!!.isNotEmpty()) {
                UIUtil.hideKeyboard(requireActivity())
                /**CRIANDO O OBJETO A SER ENVIADO ->*/
                mProcess = RequestInventoryReadingProcess(
                    mArgs.clickAdapter!!.id,
                    numeroContagem = mArgs.clickAdapter!!.numeroContagem,
                    idEndereco = mIdAndress, // --> PRIMEIRA LEITURA == NULL
                    codigoBarras = barcode.toString()
                )
                mBinding!!.editQrcode.setText("")
                /**ENVIANDO OBJETO  ->*/
                mViewModel.readingQrCode(
                    inventoryReadingProcess = mProcess
                )
                mBinding!!.editQrcode.setText("")
            }
        }
    }

    /**
     * EM GERAL O PRIMEIRO ITEM SEMPRE ENVIARA (NULL) COMO idEndereco,APOS SEGUNDA LEITURA PASSARA
     * O idEndereco DO ULTIMO OBJETO LIDO, PARA COMPARAR E CHAMAR DIALOG SE DESEJA TROCAR O ENDEREO OU NAO.
     * QUANDO LER UM EAN OU PRODUTO ELE ENVIARA O idEndereco CORRENTE,POREM O OBJETO RECEBIDO CONTERA
     * idEndereco(NULL),ENTAO FOI CRIADO A VAR (mNewResultObjIfNull) PARA PEGAR E CRIAR UM NOVO OBJETO QUE
     * CONTERA A MESCLA DOS ULTIMO LIDO E DO CORRENTE,ASSIM SEMPRE CONTERA (codigoBarras,idEndereco,enderecoVisual)
     * NAO NULOS.
     */
    private fun setObservable() {
        /**SUCESSO LEITURA -->*/
        mViewModel.mSucessShow.observe(viewLifecycleOwner) { response ->
            //Quando objeto vir com idEndereço null -->
            if (response.result.enderecoVisual == null) {
                mNewResultObjIfNull = ProcessaLeituraResponseInventario2(
                    codigoBarras = mNewResultObj.codigoBarras,
                    idEndereco = mNewResultObj.idEndereco,
                    enderecoVisual = mNewResultObj.enderecoVisual,
                    idInventarioAbastecimentoItem = response.result.idInventarioAbastecimentoItem,
                    idProduto = response.result.idProduto,
                    layoutEtiqueta = response.result.layoutEtiqueta,
                    numeroSerie = response.result.numeroSerie,
                    sku = response.result.sku,
                    produtoPronto = response.result.produtoPronto,
                    produtoVolume = response.result.produtoVolume,
                    EAN = response.result.EAN
                )
                setViews(mNewResultObjIfNull, response.leituraEnderecoCreateRvFrag2)
                clickButton(mNewResultObjIfNull)
            } else {
                mNewResultObj = response.result
                clickButton(mNewResultObj)
                //Quando objeto NAO vir com idEndereço null e for a primeira leitura -->
                if (mIdAndress == null) {
                    mIdAndress = response.result.idEndereco
                    setViews(response.result, response.leituraEnderecoCreateRvFrag2)
                    //Validar se chama o dialog de troca de endereço ou nao -->
                } else {
                    if (mIdAndress == response.result.idEndereco || response.result.idEndereco == null || response.result.idEndereco == 0) {
                        setViews(response.result, response.leituraEnderecoCreateRvFrag2)
                    } else {
                        alertDialog(response.result.idEndereco)
                    }
                }
            }
        }

        mViewModel.mSucessComparationShow2.observe(viewLifecycleOwner) { responseDialog ->
            mNewResultObj = responseDialog.result
            setViews(responseDialog.result, responseDialog.leituraEnderecoCreateRvFrag2)

        }

        /**ERRO LEITURA -->*/
        mViewModel.mErrorShow.observe(viewLifecycleOwner) { messageError ->
            vibrateExtension(500)
            CustomAlertDialogCustom().alertMessageErrorSimples(requireContext(), messageError)
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

    private fun setViews(
        response: ProcessaLeituraResponseInventario2,
        leituraEnderecoCreateRvFrag2: List<LeituraEndInventario2List>
    ) {
        if (response.produtoPronto == null) {
            mBinding!!.itTxtProdutos.text = "0"
        } else {
            mBinding!!.itTxtProdutos.text = response.produtoPronto.toString()
        }
        mBinding!!.itTxtEndereco.text = response.enderecoVisual
        mBinding!!.linearParent.visibility = View.VISIBLE
        mBinding!!.linearButton.visibility = View.VISIBLE
        mBinding!!.itTxtVolumes.text = response.produtoVolume.toString()
        mAdapter.submitList(leituraEnderecoCreateRvFrag2)
    }

    private fun hideViews() {
        mBinding.apply {
            this!!.linearButton.visibility = View.INVISIBLE
            linearParent.visibility = View.INVISIBLE
        }
    }

    /**
     * ENDEREÇO VISUAL APOS LER UM EAN RETURN NULL
     */

    private fun alertDialog(mNewIdEndereco: Int) {
        vibrateExtension(500)
        CustomMediaSonsMp3().somError(requireContext())
        val mAlert = AlertDialog.Builder(requireContext())
        mAlert.setCancelable(false)
        val mBindinginto =
            LayoutAlertAtencaoOptionsBinding.inflate(LayoutInflater.from(requireContext()))
        mAlert.setView(mBindinginto.root)
        val mShow = mAlert.show()
        mBindinginto.txtMessageAtencao.text = getString(R.string.deseja_manter_endereço)
        mBindinginto.buttonSimAlert.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Cod.${mProcess.codigoBarras} mantido.",
                Toast.LENGTH_SHORT
            ).show()
            mShow.dismiss()
        }
        mBindinginto.buttonNaoAlert.setOnClickListener {
            val newObj = RequestInventoryReadingProcess(
                idEndereco = mNewIdEndereco,
                numeroContagem = mProcess.numeroContagem,
                idInventario = mProcess.idInventario,
                codigoBarras = mProcess.codigoBarras
            )
            mIdAndress = mNewIdEndereco
            mViewModel.readingQrCodeDialog(newObj)
            mBinding!!.editQrcode.setText("")
            mShow.hide()
        }
    }

    private fun clickButton(responseQrCode: ProcessaLeituraResponseInventario2) {
        mBinding!!.apply {
            buttonVerEnd.setOnClickListener {
                CustomMediaSonsMp3().somClick(requireContext())
                vibrateExtension(100)
                val action = InventoryReadingFragment2Directions.clickShowAndress(
                    responseQrCode,
                    mArgs.clickAdapter!!
                )
                findNavController().navAnimationCreate(action)
            }
            buttonAvulso.setOnClickListener {
                mViewModel.mSucessShow.removeObservers(viewLifecycleOwner)
                CustomMediaSonsMp3().somClick(requireContext())
                val action =
                    InventoryReadingFragment2Directions.clickCreateVoid(
                        responseQrCode = responseQrCode,
                        mArgs.clickAdapter!!,
                    )
                findNavController().navAnimationCreate(action)
            }
        }
    }


}