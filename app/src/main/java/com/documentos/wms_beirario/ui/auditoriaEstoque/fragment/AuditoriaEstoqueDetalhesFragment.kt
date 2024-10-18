package com.documentos.wms_beirario.ui.auditoriaEstoque.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentoAuditoriaEstoqueDetalhesBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ResponseAuditoriaEstoqueAP
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoqueDetalhes
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueDetalhesViewModel
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar


class AuditoriaEstoqueDetalhesFragment(
    private val detalhes: ResponseAuditoriaEstoqueAP,
    private val token: String,
    private val auditoria: ListaAuditoriasItem,
    private val idArmazem: Int,
    private val idEndereco: Int
) : DialogFragment() {


    private var _binding: FragmentoAuditoriaEstoqueDetalhesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterDetalhes: AdapterAuditoriaEstoqueDetalhes
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var dialogProgress: Dialog
    private lateinit var viewModel: AuditoriaEstoqueDetalhesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoAuditoriaEstoqueDetalhesBinding.inflate(layoutInflater)
        setToolbar()
        initConst()
        setRv()
        getData()
        observer()

        return binding.root
    }

    private fun getData() {
        try {
            viewModel.getDetalhes(
                idArmazem = idArmazem,
                token = token,
                idAuditoriaEstoque = auditoria.id,
                idEndereco = idEndereco.toString(),
                idProduto = detalhes.idProduto.toString()
            )
        } catch (e: Exception) {
            alertDialog.alertMessageErrorSimplesAction(
                requireContext(),
                "Ocorreu um erro ao realizar a chamada dos detalhes:\n${e.toString()}",
                action = {
                    dialog?.dismiss()
                }
            )
        }

    }


    private fun initConst() {
        adapterDetalhes = AdapterAuditoriaEstoqueDetalhes()
        dialogProgress =
            CustomAlertDialogCustom().progress(requireContext(), "Buscando estantes...")
        dialogProgress.show()
        sharedPreferences = CustomSharedPreferences(requireContext())
        alertDialog = CustomAlertDialogCustom()
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueDetalhesViewModel.AuditoriaEstoqueDetalhesViewModelFactory(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueDetalhesViewModel::class.java]
    }

    private fun setToolbar() {
        binding.toolbarDetail.apply {
            title = "Detalhes"
            subtitle = requireActivity().getVersionNameToolbar()
            setNavigationOnClickListener {
                dialog?.dismiss()
            }
        }
    }

    private fun setRv() {
        binding.rvDetalhes.apply {
            adapter = adapterDetalhes
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun observer() {
        viewModel.apply {
            responseGetDetalhes()
            errorAll()
            errorDb()
            progress()
        }
    }

    private fun AuditoriaEstoqueDetalhesViewModel.responseGetDetalhes() {
        sucessGetDetalhesShow.observe(requireActivity()) { resp ->
            if (resp != null) {
                if (resp.isNotEmpty()) {
                    binding.txtInfo.visibility = View.GONE
                    adapterDetalhes.update(resp)
                } else {
                    binding.txtInfo.visibility = View.VISIBLE
                }
            } else {
                binding.txtInfo.apply {
                    visibility = View.VISIBLE
                    text = "Erro ao receber detalhes"
                }
            }
        }
    }

    private fun AuditoriaEstoqueDetalhesViewModel.errorAll() {
        errorAllShow.observe(requireActivity()) { resp ->
            alertDialog.alertMessageErrorSimples(requireContext(), resp)
        }
    }

    private fun AuditoriaEstoqueDetalhesViewModel.errorDb() {
        errorAllShow.observe(requireActivity()) { resp ->
            alertDialog.alertMessageErrorSimples(requireContext(), resp)
        }
    }

    private fun AuditoriaEstoqueDetalhesViewModel.progress() {
        progressShow.observe(requireActivity()) { progress ->
            binding.progressDetalhes.isVisible = progress
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        dialogProgress.dismiss()
    }
}