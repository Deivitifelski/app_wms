package com.documentos.wms_beirario.ui.configuracoes.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentSettings1Binding
import com.documentos.wms_beirario.ui.configuracoes.ActivityImpressora
import com.documentos.wms_beirario.ui.configuracoes.ControlActivity
import com.documentos.wms_beirario.utils.extensions.extensionStartActivity
import com.documentos.wms_beirario.utils.extensions.onBackTransition



class SettingsFragment1 : Fragment(R.layout.fragment_settings1) {
    private var mBinding: FragmentSettings1Binding? = null
    val binding get() = mBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettings1Binding.inflate(layoutInflater)
        setupToolbar()
        click()
        return binding.root
    }

    private fun setupToolbar() {
        mBinding!!.toolbarSetting.apply {
            setNavigationOnClickListener {
                requireActivity().onBackTransition()
            }
        }
    }

    private fun click() {
        //ENVIANDO PARA CONFIGURAÇAO TEMPERATURA -->
        mBinding!!.buttonTemperatura.setOnClickListener {
            requireActivity().extensionStartActivity(ControlActivity())
        }

        mBinding!!.buttonPrinter.setOnClickListener{
            requireActivity().extensionStartActivity(ActivityImpressora())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}