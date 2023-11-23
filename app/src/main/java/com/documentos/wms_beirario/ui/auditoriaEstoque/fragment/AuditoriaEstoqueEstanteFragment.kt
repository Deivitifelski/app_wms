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
import com.documentos.wms_beirario.ui.auditoriaEstoque.viewModels.AuditoriaEstoqueViewModel1
import com.documentos.wms_beirario.ui.auditoriaEstoque.views.AuditoriaEstoqueEnderecoActivity2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.getVersionNameToolbar


class AuditoriaEstoqueEstanteFragment(
    private val auditoriaClick: ListaAuditoriasItem?
) : DialogFragment() {


    private var _binding: FragmentAuditoriaEstoqueEstanteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterEstantes: AdapterAuditoriaEstoque2
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var alertDialog: CustomAlertDialogCustom
    private lateinit var viewModel: AuditoriaEstoqueViewModel1
    private var idArmazem: Int? = null
    private var token: String? = null


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
        setToolbar()
        initConst()
        setRv()
        getData()
        observer()
        return binding.root
    }


    private fun initConst() {
        adapterEstantes = AdapterAuditoriaEstoque2 { estante ->
            val intent = Intent(requireActivity(), AuditoriaEstoqueEnderecoActivity2::class.java)
            intent.putExtra("AUDITORIA_SELECIONADA",auditoriaClick)
            intent.putExtra("ESTANTE",estante.estante)
            startActivity(intent)
        }
        sharedPreferences = CustomSharedPreferences(requireContext())
        alertDialog = CustomAlertDialogCustom()
        idArmazem = sharedPreferences.getInt(CustomSharedPreferences.ID_ARMAZEM)
        token = sharedPreferences.getString(CustomSharedPreferences.TOKEN)
        viewModel = ViewModelProvider(
            this,
            AuditoriaEstoqueViewModel1.AuditoriaEstoqueViewModel1Factory(AuditoriaEstoqueRepository())
        )[AuditoriaEstoqueViewModel1::class.java]
    }

    private fun setToolbar() {
        binding.toolbar8.apply {
            title = "Selecione a estante"
            subtitle = requireActivity().getVersionNameToolbar()
            setNavigationOnClickListener {
                dialog?.dismiss()
            }
        }
    }

    private fun setRv() {
        binding.rvEstantes.apply {
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
            emplyAuditoriasEstantesDb()
            notEmplyAuditoriasEstantesDb()
            errorDb()
            errorAll()
            validaProgress()
        }
    }

    private fun AuditoriaEstoqueViewModel1.emplyAuditoriasEstantesDb() {
        sucessGetAuditoriaEmplyShow.observe(requireActivity()) { emply ->
            alertDialog.alertMessageErrorSimplesAction(
                requireContext(),
                "Sem estantes para auditoria selecionada\n${
                    auditoriaClick?.situacao
                } - ${auditoriaClick?.numero}" ?: emply,
                action = { dialog?.dismiss() })
        }
    }

    private fun AuditoriaEstoqueViewModel1.notEmplyAuditoriasEstantesDb() {
        sucessGetEstantesShow.observe(requireActivity()) { list ->
            adapterEstantes.update(list)
        }
    }

    private fun AuditoriaEstoqueViewModel1.errorDb() {
        errorDbShow.observe(requireActivity()) { error ->
            alertDialog.alertMessageErrorSimples(requireActivity(), error)
        }
    }

    private fun AuditoriaEstoqueViewModel1.errorAll() {
        errorAllShow.observe(requireActivity()) { error ->
            alertDialog.alertMessageErrorSimples(requireActivity(), error)
        }
    }

    private fun AuditoriaEstoqueViewModel1.validaProgress() {
        progressShow.observe(requireActivity()) { progress ->
            binding.progress.isVisible = progress
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}