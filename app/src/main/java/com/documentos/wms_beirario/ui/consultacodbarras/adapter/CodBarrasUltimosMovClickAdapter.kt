package com.documentos.wms_beirario.ui.consultacodbarras.adapter

import UltimosMovimentosModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvUltimosMovClickBinding


class CodBarrasUltimosMovClickAdapter :
    RecyclerView.Adapter<CodBarrasUltimosMovClickAdapter.UltimosMovClickViewHolder>() {

    private val mLIstUltimosMovClick = mutableListOf<UltimosMovimentosModel>()

    inner class UltimosMovClickViewHolder(val mBinding: ItemRvUltimosMovClickBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(it: UltimosMovimentosModel) {
            with(mBinding) {
                itDataUltimosmovClick.text = it.data
                itUsuarioUltimomovClick.text = it.usuario
                itCodNumeroserieUltimomovClick.text = it.numeroSerie

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UltimosMovClickViewHolder {
        val mBinding =
            ItemRvUltimosMovClickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UltimosMovClickViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: UltimosMovClickViewHolder, position: Int) {
        holder.geraItem(mLIstUltimosMovClick[position])
    }


    override fun getItemCount() = mLIstUltimosMovClick.size

    fun update(ultimosMovimentos: List<UltimosMovimentosModel?>) {
        mLIstUltimosMovClick.clear()
        this.mLIstUltimosMovClick.addAll(ultimosMovimentos as MutableList<UltimosMovimentosModel>)
        notifyDataSetChanged()
    }


}




