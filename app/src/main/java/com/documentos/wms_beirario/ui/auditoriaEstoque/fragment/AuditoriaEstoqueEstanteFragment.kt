package com.documentos.wms_beirario.ui.auditoriaEstoque.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.databinding.FragmentAuditoriaEstoqueEstanteBinding
import com.documentos.wms_beirario.model.auditoriaEstoque.response.response.ListaAuditoriasItem
import com.documentos.wms_beirario.repository.auditoriaEstoque.AuditoriaEstoqueRepository
import com.documentos.wms_beirario.ui.auditoriaEstoque.adapters.AdapterAuditoriaEstoque2
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueEstantesViewModel
import com.documentos.wms_beirario.ui.auditoriaEstoque.views.AuditoriaEstoqueEnderecoActivity2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.extensionStarActivityanimation
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar
import com.documentos.wms_beirario.utils.extensions.toastError


class AuditoriaEstoqueEstanteFragment(
    private val auditoriaClick: ListaAuditoriasItem?
) : DialogFragment() {


    private var _binding: FragmentAuditoriaEstoqueEstanteBinding? = null
    private val binding get() = _binding
    private lateinit var adapterEstantes: AdapterAuditoriaEstoque2
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueEstantesViewModel
    private var idArmazem: Int? = null
    private var token: String? = null
    private var validationBack = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuditoriaEstoqueEstanteBinding.inflate(layoutInflater)
        isCancelable = false
        setToolbar()
        initConst()
        setRv()
        getData()
        observer()
        return binding!!.root
    }


    private fun initConst() {
        adapterEstantes = AdapterAuditoriaEstoque2 { estante ->
            dialog?.dismiss()
            val intent = Intent(requireActivity(), AuditoriaEstoqueEnderecoActivity2::class.java)
            intent.putExtra("AUDITORIA_SELECIONADA", auditoriaClick)
            intent.putExtra("ESTANTE", estante.estante)
            startActivity(intent)
            requireActivity().extensionStarActivityanimation(requireActivity())
        }
        sharedPreferences = CustomSharedPreferences(requireContext())
        alertDialog = CustomAlertDialogCustom()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueEstantesViewModel.AuditoriaEstoqueEstantesViewModelFactory(
                AuditoriaEstoqueRepository()
            )
        )[AuditoriaEstoqueEstantesViewModel::class.java]
    }

    private fun setToolbar() {
        binding?.toolbar8?.apply {
            title = "Selecione a estante"
            subtitle = requireActivity().getVersionNameToolbar()
            setNavigationOnClickListener {
                if (!validationBack) {
                    dialog?.dismiss()
                } else {
                    requireActivity().toastError(
                        requireActivity(),
                        "Para sair aguarde resposta do servidor!"
                    )
                }
            }
        }
    }

    private fun setRv() {
        binding?.rvEstantes?.apply {
            adapter = adapterEstantes
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun getData() {
        if (idArmazem != null && token != null && auditoriaClick != null) {
            viewModel.getEstantes(
                idArmazem = idArmazem!!,
                token = token!!,
                idAuditoriaEstoque = auditoriaClick.id
            )
        } else {
            alertDialog.alertMessageErrorSimplesAction(
                requireContext(), "Ocorreu um erro ao buscar estantes\nSaia e tente novamente",
                action = {
                    dialog?.dismiss()
                }
            )
        }
    }

    private fun observer() {
        viewModel.apply {
            notEmplyAuditoriasEstantesDb()
            errorDb()
            errorAll()
            validaProgress()
        }
    }


    private fun AuditoriaEstoqueEstantesViewModel.notEmplyAuditoriasEstantesDb() {
        sucessGetEstantesShow.observe(requireActivity()) { list ->
            if (list != null) {
                if (list.isEmpty()) {
                    binding?.txtInfo?.visibility = View.VISIBLE
                    binding?.txtInfo?.text = "Sem estantes para endereço selecionado"
                } else {
                    binding?.txtInfo?.visibility = View.INVISIBLE
                    adapterEstantes.update(list)
                }
            }
        }
    }

    private fun AuditoriaEstoqueEstantesViewModel.errorDb() {
        errorDbShow.observe(requireActivity()) { error ->
            binding?.txtInfo?.text = error
            binding?.txtInfo?.visibility = View.VISIBLE
        }
    }

    private fun AuditoriaEstoqueEstantesViewModel.errorAll() {
        errorAllShow.observe(requireActivity()) { error ->
            binding?.txtInfo?.text = error
            binding?.txtInfo?.visibility = View.VISIBLE
        }
    }

    private fun AuditoriaEstoqueEstantesViewModel.validaProgress() {
        progressShow.observe(requireActivity()) { progress ->
            binding?.progress?.isVisible = progress
            validationBack = progress
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}