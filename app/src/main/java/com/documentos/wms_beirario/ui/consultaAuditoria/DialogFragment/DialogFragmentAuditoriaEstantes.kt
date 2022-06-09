package com.documentos.wms_beirario.ui.consultaAuditoria.DialogFragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.DialogFragmentAuditoriaEstantesBinding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaEstantes2
import com.documentos.wms_beirario.ui.consultaAuditoria.AuditoriaActivity2
import com.documentos.wms_beirario.ui.consultaAuditoria.adapter.AuditoriaAdapterEstantes2
import com.documentos.wms_beirario.utils.extensions.extensionSendActivityanimation

class DialogFragmentAuditoriaEstantes(
    val mListEstantes: List<ResponseAuditoriaEstantes2>,
    val mIdAuditoria: Int
) : DialogFragment() {

    private var mBinding: DialogFragmentAuditoriaEstantesBinding? = null
    private val binding get() = mBinding!!
    private lateinit var mAdapter: AuditoriaAdapterEstantes2

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogFragmentAuditoriaEstantesBinding.inflate(layoutInflater)
        setupDialogShow()
        return binding.root
    }

    private fun setupDialogShow() {
        mBinding!!.toolbarAuditoriaestantes.setNavigationOnClickListener {
            dismiss()
        }

        mAdapter = AuditoriaAdapterEstantes2 { estantes ->
            val intent = Intent(requireContext(), AuditoriaActivity2::class.java)
            intent.apply {
                putExtra("ID", mIdAuditoria.toString())
                putExtra("ESTANTE", estantes.estante)
            }
            requireActivity().extensionSendActivityanimation()
            startActivity(intent)
            dismiss()
        }


        mBinding!!.rvEstantes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mAdapter.update(mListEstantes as MutableList<ResponseAuditoriaEstantes2>)
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}