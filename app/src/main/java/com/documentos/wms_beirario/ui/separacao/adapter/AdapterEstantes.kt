package com.documentos.wms_beirario.ui.separacao.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEstanteSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ResponseEstantes
import com.documentos.wms_beirario.model.separation.ResponseEstantesItem

class AdapterEstantes(
    private var onClick: (List<ResponseEstantesItem>) -> Unit
) : RecyclerView.Adapter<AdapterEstantes.SeparacaoItemViewHolderEst>() {

    var mListEstantesCheck = mutableListOf<String>()
    var mList = mutableListOf<ResponseEstantesItem>()

    inner class SeparacaoItemViewHolderEst(val mBinding: ItemRvEstanteSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ResponseEstantesItem) {
            mBinding.itAndarSeparacao1.text = checks.estante
            mBinding.checkboxSeparacao1.isChecked = checks.status

            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                } else {
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                }
            }
            itemView.setOnClickListener {
                if (!mBinding.checkboxSeparacao1.isChecked) {
                    mBinding.checkboxSeparacao1.isChecked = true
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                } else {
                    mBinding.checkboxSeparacao1.isChecked = false
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeparacaoItemViewHolderEst {
        val mBinding =
            ItemRvEstanteSeparacaoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SeparacaoItemViewHolderEst(mBinding)
    }

    override fun onBindViewHolder(holder: SeparacaoItemViewHolderEst, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = mList.size

    fun setCkeckBox(estantesCheckBox: List<String>?) {
        mListEstantesCheck = estantesCheckBox as MutableList<String>
        mList.forEach { estanteInit ->
            estanteInit.status = mListEstantesCheck.contains(estanteInit.estante)
        }
        notifyDataSetChanged()
    }

    fun selectAll() {
        try {
            mList.forEach { check ->
                check.status = true
            }
            onClick.invoke(mList)
            notifyDataSetChanged()
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun unSelectAll() {
        try {
            mList.forEach { check ->
                check.status = false
            }
            notifyDataSetChanged()
            onClick.invoke(mList)
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun update(itensCheckBox: ResponseEstantes) {
        mList.clear()
        mList.addAll(itensCheckBox)
        notifyDataSetChanged()
    }
}





