package com.documentos.wms_beirario.ui.inventario.fragment.createVoid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentCreateVoidBinding
import com.documentos.wms_beirario.extensions.onBackTransition


class CreateVoidInventoryFragment : Fragment() {


    private var mBinding: FragmentCreateVoidBinding? = null
    private val _binding get() = mBinding!!
    private lateinit var mFragmentAddCreateVoid: Fragment
    private lateinit var mFragmentAddedCreateVoid: Fragment
    private lateinit var mCurrenteFragment: Fragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCreateVoidBinding.inflate(inflater, container, false)
        mBinding!!.buttonAdicionarInventoryCreate.isChecked = true
        setupNavigation()
        /**CONFIGURANDO INICIO DOS FRAGMENTOS VISIBILIDADES--> */
        requireActivity().supportFragmentManager.beginTransaction().apply {
            add(R.id.container_create_void, mFragmentAddedCreateVoid, "1").hide(
                mFragmentAddedCreateVoid
            )
            add(R.id.container_create_void, mFragmentAddCreateVoid, "0")
                .commit()
        }
        setupToolbar()
        return _binding.root
    }

    private fun setupToolbar() {
        mBinding!!.toolbar.setNavigationOnClickListener {
            requireActivity().onBackTransition()
        }
    }

    private fun setupNavigation() {
        mFragmentAddCreateVoid = AddVoidFragment()
        mFragmentAddedCreateVoid = AddedVoidFragment()
        mCurrenteFragment = mFragmentAddCreateVoid
        /**ALTERANDO A VISIBILIDADE -->*/
        mBinding!!.buttonsNavigationCreateVoid.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) when (checkedId) {
                R.id.button_Adicionar_inventory_create -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .hide(mCurrenteFragment).show(mFragmentAddCreateVoid)
                        .setTransition(TRANSIT_FRAGMENT_OPEN).commit()
                    mCurrenteFragment = mFragmentAddCreateVoid
                }
                R.id.button_Adicionados_inventory_create -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .hide(mCurrenteFragment).show(mFragmentAddedCreateVoid)
                        .setTransition(TRANSIT_FRAGMENT_OPEN).commit()
                    mCurrenteFragment = mFragmentAddedCreateVoid
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }


}