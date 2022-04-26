package com.documentos.wms_beirario.ui.etiquetagem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvLabeling3Binding
import com.documentos.wms_beirario.databinding.ItemRvPendenciaNfBinding
import com.documentos.wms_beirario.databinding.ItemRvPendenciaPedidoBinding
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse2
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse3
import com.documentos.wms_beirario.model.etiquetagem.response.ResponsePendencePedidoEtiquetagemItem

class AdapterLabelingPendencyNF() : ListAdapter<ResponsePendencePedidoEtiquetagemItem, AdapterLabelingPendencyNF.AdapterPendingViewHolderNF>(DiffUtilPendingNF()) {

    inner class AdapterPendingViewHolderNF(val mBinding: ItemRvPendenciaPedidoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponsePendencePedidoEtiquetagemItem) {
            mBinding.numeroPedidoApi.text = item.numeroPedido.toString()
            mBinding.quantidadeVolumes.text = item.quantidadeVolumes.toString()
            mBinding.quantidadePendente.text = item.quantidadePendente.toString()
            mBinding.normativaApi.text = item.tipoPedido .toString()
            if (item.numeroNormativa == null){
                mBinding.numeroNormativaApi.text = "-"
            }else {
                mBinding.numeroNormativaApi.text = item.numeroNormativa.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPendingViewHolderNF {
        val binding =
            ItemRvPendenciaPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPendingViewHolderNF(binding)
    }

    override fun onBindViewHolder(holder: AdapterPendingViewHolderNF, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilPendingNF() : DiffUtil.ItemCallback<ResponsePendencePedidoEtiquetagemItem>() {
    override fun areItemsTheSame(
        oldItem: ResponsePendencePedidoEtiquetagemItem,
        newItem: ResponsePendencePedidoEtiquetagemItem
    ): Boolean {
        return oldItem.numeroPedido == newItem.numeroPedido
    }

    override fun areContentsTheSame(
        oldItem: ResponsePendencePedidoEtiquetagemItem,
        newItem: ResponsePendencePedidoEtiquetagemItem
    ): Boolean {
        return oldItem == newItem
    }

}
