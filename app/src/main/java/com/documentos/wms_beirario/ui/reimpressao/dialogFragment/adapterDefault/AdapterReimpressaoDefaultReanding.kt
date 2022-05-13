package com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvReimpressaoNumSerieBinding
import com.documentos.wms_beirario.model.reimpressao.ResultReimpressaoDefaultItem

class AdapterReimpressaoDefaultReanding(val onClick: (ResultReimpressaoDefaultItem) -> Unit) :
    ListAdapter<ResultReimpressaoDefaultItem, AdapterReimpressaoDefaultReanding.AdapterReimpressaoNumSerieVH>(
        DiffUltilCallBackReimpressaoDefault()
    ) {

    inner class AdapterReimpressaoNumSerieVH(val mBinding: ItemRvReimpressaoNumSerieBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResultReimpressaoDefaultItem) {
            with(mBinding) {
                descricaoTipoDocumentoApi.text = item.descricaoTipoDocumento
                documentoApi.text = item.documento
                numSerieApi.text = item.numeroSerie
                numTdocApi.text = item.tipoDocumento
            }


            itemView.setOnClickListener {
                onClick.invoke(item)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterReimpressaoNumSerieVH {
        val binding =
            ItemRvReimpressaoNumSerieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AdapterReimpressaoNumSerieVH(binding)
    }

    override fun onBindViewHolder(holder: AdapterReimpressaoNumSerieVH, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUltilCallBackReimpressaoDefault() :
    DiffUtil.ItemCallback<ResultReimpressaoDefaultItem>() {
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


