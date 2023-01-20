package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.databinding.FragmentEnderecoBinding
import com.documentos.wms_beirario.databinding.FragmentProdutoClickBinding
import com.documentos.wms_beirario.databinding.FragmentUltimosMovClickBinding
import com.documentos.wms_beirario.databinding.FragmentVolumesClickBinding
import com.documentos.wms_beirario.model.codBarras.EnderecoModel
import com.documentos.wms_beirario.model.codBarras.VolumesModel
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasProdutosClickAdapter
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasUltimosMovClickAdapter
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasVolumeClickAdapter
import com.documentos.wms_beirario.utils.CustomSnackBarCustom


class EnderecoFragment : Fragment() {

    private var mBinding: FragmentEnderecoBinding? = null
    private val _binding get() = mBinding!!
    private lateinit var mDados: EnderecoModel
    private lateinit var mAdapterVolumeClick: CodBarrasVolumeClickAdapter
    private lateinit var mAdapterProdutoClick: CodBarrasProdutosClickAdapter
    private lateinit var mAdapterUltimosMovimentosClick: CodBarrasUltimosMovClickAdapter
    private var mQntVol: Int = 0
    private var mQntProd: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEnderecoBinding.inflate(inflater, container, false)
        initDados()
        clickButtons()
        initItensFixos(mDados)
        return _binding.root
    }

    private fun initDados() {
        try {
            val args = this.arguments
            if (args != null) {
                mDados = args.getSerializable("ENDERECO") as EnderecoModel
            } else {
                CustomSnackBarCustom().snackBarPadraoSimplesBlack(
                    requireView(), "Erro com os dados!"
                )
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButtons() {
        mBinding!!.buttonvolumesUltimosMovimentos.setOnClickListener {
            bottomSheetDialogUltimosVolumesClick()
        }
        mBinding!!.buttonvolumesProduto.setOnClickListener {
            bottomSheetProdutoClick()
        }
        mBinding!!.buttonvolumesEndereco.setOnClickListener {
            bottomSweetVolumesClick()
        }
    }

    private fun initItensFixos(mDados: EnderecoModel) {
        mBinding?.itTipoCodBarrasEndereco?.text = mDados.tipo.toString()
        mBinding?.itNomeAreaCodBarrasEndereco?.text = mDados.nomeArea.toString()
        mBinding?.itEnderecoVisualCodBarrasEndereco?.text = mDados.enderecoVisual.toString()
        //SETANDO A QUANTIDADE NOS BUTTONS -->
        mDados.produtos.forEach { quant ->
            mQntProd += quant.quantidade
        }
        mDados.volumes.forEach { qnt ->
            if (qnt?.quantidade != null) {
                mQntVol += qnt.quantidade
            }
        }
        mBinding?.txtVolCount?.text = "$mQntVol" ?: ""
        mBinding?.txtProdCount?.text = "$mQntProd" ?: ""

    }

    //VOLUMES -->
    private fun bottomSweetVolumesClick() {
        val mAlert = AlertDialog.Builder(requireContext())
        val mInflater = FragmentVolumesClickBinding.inflate(LayoutInflater.from(requireContext()))
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val largura: Int = size.x
        val altura: Int = size.y
        //LARGURA
        mInflater.root.maxWidth = largura
        mInflater.root.minWidth = largura
        //ALTURA
        mInflater.root.maxHeight = (altura * 0.8).toInt()
        mInflater.root.minHeight = (altura * 0.8).toInt()
        mAlert.setView(mInflater.root)
        mAlert.show()
        mInflater.txtTotal.text = "Total de volumes: $mQntVol"
        val mRecyclerView = mInflater.rvVolumesClick
        val mTxtInformativo = mInflater.txtInformativoVolumesClick
        val mImagem = mInflater.imageLottieVolumeClick
        mAdapterVolumeClick = CodBarrasVolumeClickAdapter(requireContext())
        if (mDados.volumes.isEmpty()) {
            mTxtInformativo.visibility = View.VISIBLE
            mImagem.visibility = View.VISIBLE
        } else {
            mImagem.visibility = View.INVISIBLE
            mTxtInformativo.visibility = View.INVISIBLE
            mRecyclerView.apply {
                this.layoutManager = LinearLayoutManager(activity)
                this.adapter = mAdapterVolumeClick
            }
            mAdapterVolumeClick.update(mDados.volumes as List<VolumesModel>)
        }
    }

    //PRODUTOS ----------------------------------------------------------------------------------------------->
    private fun bottomSheetProdutoClick() {
        val alert = AlertDialog.Builder(requireContext())
        val mInflater = FragmentProdutoClickBinding.inflate(LayoutInflater.from(requireContext()))
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val largura: Int = size.x
        val altura: Int = size.y
        mInflater.root.maxWidth = largura   //LARGURA
        mInflater.root.minWidth = largura
        mInflater.root.maxHeight = (altura * 0.8).toInt();   //ALTURA
        mInflater.root.minHeight = (altura * 0.8).toInt();

        alert.setView(mInflater.root)
        alert.show()
        mInflater.txtTotal.text = "Total de produtos: $mQntProd"
        val mRecyclerView = mInflater.rvProdutoClick
        val mTxtInformativo = mInflater.txtInformativoProdutoClick
        val mImagem = mInflater.imageLottieProdutoClick
        mAdapterProdutoClick = CodBarrasProdutosClickAdapter()
        if (mDados.produtos.isEmpty()) {
            mTxtInformativo.visibility = View.VISIBLE
            mImagem.visibility = View.VISIBLE
        } else {
            mImagem.visibility = View.INVISIBLE
            mTxtInformativo.visibility = View.INVISIBLE
            mRecyclerView.apply {
                this.layoutManager = LinearLayoutManager(activity)
                this.adapter = mAdapterProdutoClick
            }
            mAdapterProdutoClick.update(mDados.produtos)
        }
    }

    //ALERTA DIALOG ULTIMOS MOVIMENTOS ----------------------------------------------------------------------------------->
    @SuppressLint("InflateParams")
    private fun bottomSheetDialogUltimosVolumesClick() {
        val mAlert = AlertDialog.Builder(requireContext())
        val mInflater =
            FragmentUltimosMovClickBinding.inflate(LayoutInflater.from(requireContext()))
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val largura: Int = size.x
        val altura: Int = size.y
        mInflater.root.maxWidth = largura   //LARGURA
        mInflater.root.minWidth = largura
        mInflater.root.maxHeight = (altura * 0.8).toInt();   //ALTURA
        mInflater.root.minHeight = (altura * 0.8).toInt();
        mAlert.setView(mInflater.root)
        val mRecyclerView = mInflater.rvUltimoMovimentos
        val mTxtInformativo = mInflater.txtInformativoUltimomovimentosClick
        val mImagem = mInflater.imageLottieUltimosmovimentosClick
        mAdapterUltimosMovimentosClick = CodBarrasUltimosMovClickAdapter()
        if (mDados.ultimosMovimentos.isEmpty()) {
            mTxtInformativo.visibility = View.VISIBLE
            mImagem.visibility = View.VISIBLE
        } else {
            mImagem.visibility = View.INVISIBLE
            mTxtInformativo.visibility = View.INVISIBLE
            mRecyclerView.apply {
                this.layoutManager = LinearLayoutManager(activity)
                this.adapter = mAdapterUltimosMovimentosClick
            }
            mAdapterUltimosMovimentosClick.update(mDados.ultimosMovimentos)
        }
        mAlert.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}