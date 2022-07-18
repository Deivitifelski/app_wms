package com.documentos.wms_beirario.ui.separacao.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEstanteSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem

class AdapterSeparacaoEstantesItens(
    private var onClick: (List<ResponseItemsSeparationItem>) -> Unit
) :
    RecyclerView.Adapter<AdapterSeparacaoEstantesItens.SeparacaoItemViewHolder>() {

    var mListItensClicksSelect = mutableListOf<String>()
    var mList = mutableListOf<ResponseItemsSeparationItem>()

    inner class SeparacaoItemViewHolder(val mBinding: ItemRvEstanteSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ResponseItemsSeparationItem) {
            if (mListItensClicksSelect.contains(checks.estante)) {
                mBinding.checkboxSeparacao1.isChecked = true
                mBinding.itEstanteSeparacao1.text = checks.estante
            } else {
                mBinding.checkboxSeparacao1.isChecked = false
                mBinding.itEstanteSeparacao1.text = checks.estante
            }


            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    if (mListItensClicksSelect.contains(checks.estante)) {
                        mListItensClicksSelect.remove(checks.estante)
                    } else {
                        mListItensClicksSelect.add(checks.estante)
                    }
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                    Log.e("CHECK TRUE ||", checks.estante)
                } else {
                    mListItensClicksSelect.remove(checks.estante)
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                    Log.e("CHECK false ||", checks.estante)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeparacaoItemViewHolder {
        val mBinding =
            ItemRvEstanteSeparacaoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SeparacaoItemViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: SeparacaoItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = mList.size

    fun setCkeckBox(estantesCheckBox: List<String>) {
        mListItensClicksSelect = estantesCheckBox as MutableList<String>
    }

    fun selectAll() {
        try {
            mList.forEach { check ->
                check.status = true
                if (mListItensClicksSelect.contains(check.estante)) {
                    mListItensClicksSelect.remove(check.estante)
                } else {
                    mListItensClicksSelect.add(check.estante)
                }
                onClick.invoke(mList)
            }
            notifyDataSetChanged()
            Log.e("TAG", "selectAll: ${mList.size} + $mListItensClicksSelect")
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun unSelectAll() {
        try {
            mList.forEach { check ->
                check.status = false
                mListItensClicksSelect.remove(check.estante)
            }
            notifyDataSetChanged()
            onClick.invoke(mList)
            Log.e("TAG", "selectAll: ${mList.size} + $mListItensClicksSelect")
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun update(itensCheckBox: List<ResponseItemsSeparationItem>) {
        mList.clear()
        mList.addAll(itensCheckBox)
        notifyDataSetChanged()
    }
}




