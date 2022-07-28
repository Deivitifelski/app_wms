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
            //VERIFICA QUANDO VOLTA A TELA SE A LISTA SELECIONA CONTEM NA RECYCLAGEM ->
            mBinding.appCompatTextView17.text = "Estante"
            if (mListEstantesCheck.contains(checks.estante)) {
                mBinding.itAndarSeparacao1.text = checks.estante
                mBinding.checkboxSeparacao1.isChecked = true
            } else {
                mBinding.itAndarSeparacao1.text = checks.estante
                mBinding.checkboxSeparacao1.isChecked = checks.status
            }

            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    mListEstantesCheck.add(checks.estante)
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                } else {
                    mListEstantesCheck.remove(checks.estante)
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

    fun setCkeckBox(estantesCheckBox: List<String>) {
        mListEstantesCheck = estantesCheckBox as MutableList<String>
    }

    fun selectAll() {
        try {
            mList.forEach { check ->
                check.status = true
                if (mListEstantesCheck.contains(check.estante)) {
                    mListEstantesCheck.remove(check.estante)
                    mListEstantesCheck.add(check.estante)
                } else {
                    mListEstantesCheck.add(check.estante)
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
                mListEstantesCheck.remove(check.estante)
            }
            notifyDataSetChanged()
            onClick.invoke(mList)
            Log.e("TAG", "selectAll: ${mList.size} + $mListEstantesCheck")
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





