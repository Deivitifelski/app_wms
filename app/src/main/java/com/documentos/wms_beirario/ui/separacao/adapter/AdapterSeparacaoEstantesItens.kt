package com.documentos.wms_beirario.ui.separacao.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEstanteSeparacaoBinding
import com.documentos.wms_beirario.databinding.ItemRvInnerAndaresSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ItensResponse1
import com.documentos.wms_beirario.model.separation.ResponseGetAndaresSeparationItem

class AdapterSeparacaoEstantesItens(
    private var onClick: (List<ItensResponse1>) -> Unit
) :
    RecyclerView.Adapter<AdapterSeparacaoEstantesItens.SeparacaoItemViewHolder>() {

    var mListEstantesCheck = mutableListOf<String>()
    var mList = mutableListOf<ItensResponse1>()

    inner class SeparacaoItemViewHolder(val mBinding: ItemRvEstanteSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ItensResponse1) {
            mBinding.rvInner.apply {
                layoutManager = GridLayoutManager(context, 4)
                adapter =
                    AdapterSeparationInner(checks.andares as ArrayList<ResponseGetAndaresSeparationItem>)
            }
            mBinding.itEstanteSeparacao1.text = checks.estante
            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    mBinding.linearRvInner.visibility = View.VISIBLE
                    mListEstantesCheck.add(checks.estante)
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                    Log.e("CHECK TRUE ESTANTE||", "${checks.estante} POS: $layoutPosition")
                } else {
                    mBinding.linearRvInner.visibility = View.GONE
                    mListEstantesCheck.remove(checks.estante)
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                    Log.e("CHECK FALSE ESTANTE||", "${checks.estante} POS: $layoutPosition")
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

    fun update(itensCheckBox: List<ItensResponse1>) {
        mList.clear()
        mList.addAll(itensCheckBox)
        notifyDataSetChanged()
    }
}

/**
 * INIT RV SEPARAÇÃO 1 -->
 */
class AdapterSeparationInner(andares: ArrayList<ResponseGetAndaresSeparationItem>) :
    RecyclerView.Adapter<AdapterSeparationInner.VH>() {
    private var list = andares
    var mListAndresCheck = mutableListOf<String>()

    inner class VH(val mBinding: ItemRvInnerAndaresSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseGetAndaresSeparationItem) {
            mBinding.itAndar.text = item.andar
            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    Log.e("CHECK TRUE ESTANTE||", "${item.andar} POS: $layoutPosition")
                    mListAndresCheck.add(item.andar)
                } else {
                    Log.e("CHECK FALSE ESTANTE||", "${item.andar} POS: $layoutPosition")
                    mListAndresCheck.remove(item.andar)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRvInnerAndaresSeparacaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        !holder.isRecyclable
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

}




