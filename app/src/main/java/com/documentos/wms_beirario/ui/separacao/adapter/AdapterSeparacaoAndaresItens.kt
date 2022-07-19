package com.documentos.wms_beirario.ui.separacao.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAndarSeparacaoBinding
import com.documentos.wms_beirario.databinding.ItemRvEstanteSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ResponseGetAndaresSeparationItem

class AdapterSeparacaoAndaresItens(
    private var onClick: (List<ResponseGetAndaresSeparationItem>) -> Unit
) :
    RecyclerView.Adapter<AdapterSeparacaoAndaresItens.SeparacaoAndaresItemViewHolder>() {

    var mListItensAndaresClicksSelect = mutableListOf<String>()
    var mList = mutableListOf<ResponseGetAndaresSeparationItem>()

    inner class SeparacaoAndaresItemViewHolder(val mBinding: ItemRvAndarSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ResponseGetAndaresSeparationItem) {
            if (mListItensAndaresClicksSelect.contains(checks.andar)) {
                mBinding.checkboxSeparacao1Andar.isChecked = true
                mBinding.itAndarSeparacao1.text = checks.andar
            } else {
                mBinding.checkboxSeparacao1Andar.isChecked = false
                mBinding.itAndarSeparacao1.text = checks.andar
            }


            mBinding.checkboxSeparacao1Andar.setOnClickListener {
                if (mBinding.checkboxSeparacao1Andar.isChecked) {
                    if (mListItensAndaresClicksSelect.contains(checks.andar)) {
                        mListItensAndaresClicksSelect.remove(checks.andar)
                    } else {
                        mListItensAndaresClicksSelect.add(checks.andar)
                    }
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                    Log.e("CHECK TRUE ||", checks.andar)
                } else {
                    mListItensAndaresClicksSelect.remove(checks.andar)
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                    Log.e("CHECK false ||", checks.andar)
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeparacaoAndaresItemViewHolder {
        val mBinding =
            ItemRvAndarSeparacaoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SeparacaoAndaresItemViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: SeparacaoAndaresItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = mList.size

    fun setCkeckBox(estantesCheckBox: List<String>) {
        mListItensAndaresClicksSelect = estantesCheckBox as MutableList<String>
    }

    fun selectAll() {
        try {
            mList.forEach { check ->
                check.status = true
                if (mListItensAndaresClicksSelect.contains(check.andar)) {
                    mListItensAndaresClicksSelect.remove(check.andar)
                    mListItensAndaresClicksSelect.add(check.andar)
                } else {
                    mListItensAndaresClicksSelect.add(check.andar)
                }
                onClick.invoke(mList)
            }
            notifyDataSetChanged()
            Log.e("TAG", "selectAll: ${mList.size} + $mListItensAndaresClicksSelect")
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun unSelectAll() {
        try {
            mList.forEach { check ->
                check.status = false
                mListItensAndaresClicksSelect.remove(check.andar)
            }
            notifyDataSetChanged()
            onClick.invoke(mList)
            Log.e("TAG", "selectAll: ${mList.size} + $mListItensAndaresClicksSelect")
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun update(itensCheckBox: List<ResponseGetAndaresSeparationItem>) {
        mList.clear()
        mList.addAll(itensCheckBox)
        notifyDataSetChanged()
    }
}




