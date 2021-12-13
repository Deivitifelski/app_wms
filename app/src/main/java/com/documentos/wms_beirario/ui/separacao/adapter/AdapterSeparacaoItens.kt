package com.documentos.wms_beirario.ui.separacao.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ResponseItemsSeparationItem

class AdapterSeparacaoItens(private var onClick: (position: Int, ResponseItemsSeparationItem) -> Unit) :
    ListAdapter<ResponseItemsSeparationItem, AdapterSeparacaoItens.SeparacaoItemViewHolder>(
        DiffUltilCallBack()
    ) {

    var mLIstEstantesCkeckBox = mutableListOf<String>()

    inner class SeparacaoItemViewHolder(val mBinding: ItemRvSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ResponseItemsSeparationItem) {
            with(mBinding) {
                if (mLIstEstantesCkeckBox.contains(checks.estante)) {
                    mBinding.checkboxSeparacao1.isChecked = true
                }
                itEstanteSeparacao1.text = checks.estante
            }



            itemView.setOnClickListener {
                mBinding.checkboxSeparacao1.isChecked = !mBinding.checkboxSeparacao1.isChecked
                onClick.invoke(position, checks)
            }

            mBinding.checkboxSeparacao1.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    mLIstEstantesCkeckBox.add(checks.estante)
                    onClick.invoke(position, checks)
                    Log.e("ADAPTER CHECK OK --->",checks.estante)
                } else {
                    mLIstEstantesCkeckBox.remove(checks.estante)
                    onClick.invoke(position, checks)
                    Log.e("ADAPTER CHECK false --->",checks.estante)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeparacaoItemViewHolder {
        val mBinding =
            ItemRvSeparacaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeparacaoItemViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: SeparacaoItemViewHolder, position: Int) {
        val checks = getItem(position)
        holder.bind(checks)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun setCkeckBox(estantesCheckBox: List<String>) {
        estantesCheckBox.map { estante ->
            if (!mLIstEstantesCkeckBox.contains(estante)) {
                mLIstEstantesCkeckBox.add(estante)
            }
        }
    }

    fun selectAll(mListstreets: MutableList<String>) {
        mLIstEstantesCkeckBox.addAll(mListstreets)
    }


}

private class DiffUltilCallBack : DiffUtil.ItemCallback<ResponseItemsSeparationItem>() {
    override fun areItemsTheSame(
        oldItem: ResponseItemsSeparationItem,
        newItem: ResponseItemsSeparationItem
    ): Boolean {
        return oldItem.idArea == newItem.idArea

    }

    override fun areContentsTheSame(
        oldItem: ResponseItemsSeparationItem,
        newItem: ResponseItemsSeparationItem
    ): Boolean {
        return oldItem == newItem
    }

}



