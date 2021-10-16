package com.documentos.wms_beirario.ui.armazengem.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentArmazenagem01Binding
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemAdapter
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemRepository
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemViewModel
import com.documentos.wms_beirario.ui.armazengem.ArmazenagemViewModelFactory
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class ArmazenagemFragment_01 : Fragment() {
    private var mBinding: FragmentArmazenagem01Binding? = null
    private val _binding get() = mBinding!!
    private lateinit var mViewModel: ArmazenagemViewModel
    private var retrofitService = RetrofitService.getInstance()
    private lateinit var mToken: String
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mAdapter: ArmazenagemAdapter
    private var id_armazem: Int = 0

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

        mViewModel =
            ViewModelProvider(
                this,
                ArmazenagemViewModelFactory(ArmazenagemRepository(retrofitService))
            ).get(
                ArmazenagemViewModel::class.java
            )

        mSharedPreferences = CustomSharedPreferences(requireContext())
    }

    override fun onResume() {
        super.onResume()
        initScan()
        initShared()
    }

    private fun initShared() {
        mAdapter = ArmazenagemAdapter()
        mToken = mSharedPreferences.getString(CustomSharedPreferences.TOKEN) ?: ""
        id_armazem = mSharedPreferences.getInt(CustomSharedPreferences.ID_TAREFA)!!

        mViewModel.getArmazenagem(mToken,id_armazem)
        mViewModel.mSucess.observe(requireActivity(), Observer { response ->
            mBinding?.rvArmazenagem?.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = mAdapter
            }
            mAdapter.update(response)
        })

        mViewModel.messageError.observe(requireActivity(), Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun initScan() {
        mBinding!!.editTxtArmazem01.addTextChangedListener {
            if (it.toString() != "") {
                val qrcodeLido = mAdapter.procurarDestino(it.toString())
                if (qrcodeLido == null) {
                    Handler().postDelayed({
                        CustomAlertDialogCustom().alertMessageErrorCancelFalse(
                            requireContext(),
                            "Leia um endereço válido!"
                        )
                    }, 200)


                } else {
                    Handler().postDelayed({
                        CustomMediaSonsMp3().somSucess(requireContext())
                        abrirArmazem2(qrcodeLido)
                    }, 120)
                }
                setEdit()
            }
        }
    }

    private fun setEdit() {
        mBinding?.editTxtArmazem01?.setText("")
        mBinding?.editTxtArmazem01?.requestFocus()
    }

    private fun abrirArmazem2(qrcodeLido: ArmazenagemResponse?) {
        val bundle = Bundle()
        bundle.putSerializable("armazenagem01", qrcodeLido)
        findNavController().navigate(R.id.armazenagemFragment_02, bundle)
    }

    companion object {
        const val ID_TAREFA = "id_tarefa_clicada"
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}