package com.documentos.wms_beirario.ui.etiquetagem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvPendenciaOndaBinding
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemResponse2
import com.documentos.wms_beirario.model.etiquetagem.ResponsePendencyOndaEtiquetagemItem
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterPendingOnda() :
    ListAdapter<ResponsePendencyOndaEtiquetagemItem, AdapterPendingOnda.AdapterPendingOnda>(
        DiffUtilPendingOnda()
    ) {

    inner class AdapterPendingOnda(val mBinding: ItemRvPendenciaOndaBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponsePendencyOndaEtiquetagemItem) {
            with(mBinding) {
                item.let {
                    numeroPedidoApi.text = item.numeroOnda
                    quantidadeDocumentos.text = item.quantidadeDocumentos.toString()
                    quantidadeVolumes.text = item.quantidadeVolumes.toString()
                    quantidadePendente.text = item.quantidadePendente.toString()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPendingOnda {
        val binding =
            ItemRvPendenciaOndaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPendingOnda(binding)
    }

    override fun onBindViewHolder(holder: AdapterPendingOnda, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilPendingOnda() : DiffUtil.ItemCallback<ResponsePendencyOndaEtiquetagemItem>() {
    override fun areItemsTheSame(
        oldItem: ResponsePendencyOndaEtiquetagemItem,
        newItem: ResponsePendencyOndaEtiquetagemItem
    ): Boolean {
        return oldItem.numeroOnda == newItem.numeroOnda
    }

    override fun areContentsTheSame(
        oldItem: ResponsePendencyOndaEtiquetagemItem,
        newItem: ResponsePendencyOndaEtiquetagemItem
    ): Boolean {
        return oldItem == newItem
    }

}
