package com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvReimpressaoBinding
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefaultItem

class AdapterReimpressaoNumSerieReanding(val onClick: (ResultReimpressaoDefaultItem) -> Unit) :
    ListAdapter<ResultReimpressaoDefaultItem, AdapterReimpressaoNumSerieReanding.AdapterReimpressaoDefaultVH>(
        DiffUltilCallBackNumSerie()
    ) {

    inner class AdapterReimpressaoDefaultVH(val mBinding: ItemRvReimpressaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResultReimpressaoDefaultItem) {
            mBinding.descricaoTipoDocumentoApi.text = item.descricaoTipoDocumento
            mBinding.documentoApi.text = item.documento

            itemView.setOnClickListener {
                onClick.invoke(item)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterReimpressaoDefaultVH {
        val binding =
            ItemRvReimpressaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterReimpressaoDefaultVH(binding)
    }

    override fun onBindViewHolder(holder: AdapterReimpressaoDefaultVH, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUltilCallBackNumSerie() : DiffUtil.ItemCallback<ResultReimpressaoDefaultItem>() {
    override fun areItemsTheSame(
        oldItem: ResultReimpressaoDefaultItem,
        newItem: ResultReimpressaoDefaultItem
    ): Boolean {
        return oldItem.documento == newItem.documento
    }

    override fun areContentsTheSame(
        oldItem: ResultReimpressaoDefaultItem,
        newItem: ResultReimpressaoDefaultItem
    ): Boolean {
        return oldItem == newItem
    }


}


