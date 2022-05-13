package com.documentos.wms_beirario.ui.consultacodbarras.fragments

import EnderecoModel
import VolumesModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.FragmentEnderecoBinding
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasProdutosClickAdapter
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasUltimosMovClickAdapter
import com.documentos.wms_beirario.ui.consultacodbarras.adapter.CodBarrasVolumeClickAdapter
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.google.android.material.bottomsheet.BottomSheetDialog

class EnderecoFragment : Fragment() {

    private var mBinding: FragmentEnderecoBinding? = null
    private val _binding get() = mBinding!!
    private lateinit var mDados: EnderecoModel
    private lateinit var mAdapterVolumeClick: CodBarrasVolumeClickAdapter
    private lateinit var mAdapterProdutoClick: CodBarrasProdutosClickAdapter
    private lateinit var mAdapterUltimosMovimentosClick: CodBarrasUltimosMovClickAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEnderecoBinding.inflate(inflater, container, false)
        return _binding.root
    }


    override fun onResume() {
        super.onResume()
        initDados()
        clickButtons()
        initItensFixos(mDados)
    }

    private fun initDados() {
        try {
            val args = this.arguments
            if (args != null) {
                mDados = args.getSerializable("ENDERECO") as EnderecoModel
            } else {
                CustomSnackBarCustom().snackBarPadraoSimplesBlack(
                    requireView(),
                    "Erro com os dados!"
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
    }

    private fun bottomSweetVolumesClick() {
        val mAlert = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
        val mInflater =
            LayoutInflater.from(activity).inflate(R.layout.fragment_volumes_click, null)
        mAlert.setContentView(mInflater)
        mAlert.show()
        val mRecyclerView = mInflater.findViewById<RecyclerView>(R.id.rv_volumes_click)
        val mTxtInformativo =
            mInflater.findViewById<TextView>(R.id.txt_informativo_volumes_click)
        val mImagem =
            mInflater.findViewById<ImageView>(R.id.image_lottie_volume_click)
        mAdapterVolumeClick = CodBarrasVolumeClickAdapter()
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

    //ALERTA DIALOG PRODUTOS ----------------------------------------------------------------------------------------------->
    private fun bottomSheetProdutoClick() {
        val mBottomProduto = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
        val mInflater = LayoutInflater.from(activity).inflate(R.layout.fragment_produto_click, null)
        mBottomProduto.setContentView(mInflater)
        mBottomProduto.show()
        val mRecyclerView = mInflater.findViewById<RecyclerView>(R.id.rv_produto_click)
        val mTxtInformativo =
            mInflater.findViewById<TextView>(R.id.txt_informativo_produto_click)
        val mImagem =
            mInflater.findViewById<ImageView>(R.id.image_lottie_produto_click)
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
        val mAlert = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
        val mInflater =
            LayoutInflater.from(activity).inflate(R.layout.fragment_ultimos_mov_click, null)
        mAlert.setContentView(mInflater)
        val mRecyclerView = mInflater.findViewById<RecyclerView>(R.id.rv_ultimo_movimentos)
        val mTxtInformativo =
            mInflater.findViewById<TextView>(R.id.txt_informativo_ultimomovimentos_click)
        val mImagem =
            mInflater.findViewById<ImageView>(R.id.image_lottie_ultimosmovimentos_click)
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