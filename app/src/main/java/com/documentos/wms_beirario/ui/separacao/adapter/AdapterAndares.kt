package com.documentos.wms_beirario.ui.separacao.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEstanteSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ResponseAndaresItem

class AdapterAndares(
    private var onClick: (List<ResponseAndaresItem>) -> Unit
) : RecyclerView.Adapter<AdapterAndares.SeparacaoItemViewHolder>() {

    var mListEstantesCheck = mutableListOf<String>()
    var mList = mutableListOf<ResponseAndaresItem>()

    inner class SeparacaoItemViewHolder(val mBinding: ItemRvEstanteSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ResponseAndaresItem) {
            if (mListEstantesCheck.contains(checks.andar)) {
                mBinding.itAndarSeparacao1.text = checks.andar
                mBinding.checkboxSeparacao1.isChecked = true
            } else {
                mBinding.itAndarSeparacao1.text = checks.andar
                mBinding.checkboxSeparacao1.isChecked = checks.status
            }
            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    mListEstantesCheck.add(checks.andar)
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                    Log.e("CHECK TRUE ESTANTE||", "${checks.andar} POS: $layoutPosition")
                } else {
                    mListEstantesCheck.remove(checks.andar)
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                    Log.e("CHECK FALSE ESTANTE||", "${checks.andar} POS: $layoutPosition")
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


    //PEGANDO A LISTA RECEBIDA DA TELA ANTERIOR -->
    fun setCkeckBox(estantesCheckBox: List<String>) {
        mListEstantesCheck = estantesCheckBox as MutableList<String>
    }

    fun selectAll() {
        try {
            mList.forEach { check ->
                check.status = true
                if (mListEstantesCheck.contains(check.andar)) {
                    mListEstantesCheck.remove(check.andar)
                    mListEstantesCheck.add(check.andar)
                } else {
                    mListEstantesCheck.add(check.andar)
                }
                onClick.invoke(mList)
            }
            notifyDataSetChanged()
            Log.e("TAG", "selectAll: ${mList.size} + $mListEstantesCheck")
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun unSelectAll() {
        try {
            mList.forEach { check ->
                check.status = false
                mListEstantesCheck.remove(check.andar)
            }
            notifyDataSetChanged()
            onClick.invoke(mList)
            Log.e("TAG", "selectAll: ${mList.size} + $mListEstantesCheck")
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun update(itensCheckBox: List<ResponseAndaresItem>) {
        mList.clear()
        mList.addAll(itensCheckBox)
        notifyDataSetChanged()
    }
}





