package com.documentos.wms_beirario.ui.inventario.fragment.bottomNav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentProdutoBottomNavBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.model.inventario.ResponseListRecyclerView
import com.documentos.wms_beirario.ui.inventario.adapter.AdapterInventoryClickProduto
import com.example.coletorwms.constants.CustomSnackBarCustom


class ProdutoBottomNavFragment : Fragment() {

    private lateinit var mAdapter: AdapterInventoryClickProduto
    private lateinit var mArgs: ResponseListRecyclerView
    private var mBinding: FragmentProdutoBottomNavBinding? = null
    private val _binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProdutoBottomNavBinding.inflate(inflater, container, false)

        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        getArgs()
    }

    private fun setRecyclerView(mArgs: ResponseListRecyclerView) {
        mAdapter = AdapterInventoryClickProduto()
        mBinding!!.rvProdutoInventory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        if (mArgs.produtos.isNullOrEmpty()) {
            AppExtensions.visibilityTxt(mBinding!!.txtInf, true)
            mBinding!!.linear.visibility = View.INVISIBLE

        } else {
            mBinding!!.linear.visibility = View.VISIBLE
            AppExtensions.visibilityTxt(mBinding!!.txtInf, visibility = false)
            mAdapter.submitList(mArgs.produtos)
        }

    }

    private fun getArgs() {
            mArgs = requireArguments().getSerializable("PRODUTO_SHOW_ANDRESS") as ResponseListRecyclerView
            setRecyclerView(mArgs)
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}