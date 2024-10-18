package com.documentos.wms_beirario.ui.etiquetagem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvPendenciaPedidoBinding
import com.documentos.wms_beirario.model.etiquetagem.ResponsePendencePedidoEtiquetagemItem

class AdapterLabelingPendencyNF() :
    ListAdapter<ResponsePendencePedidoEtiquetagemItem, AdapterLabelingPendencyNF.AdapterPendingViewHolderNF>(
        DiffUtilPendingNF()
    ) {

    inner class AdapterPendingViewHolderNF(val mBinding: ItemRvPendenciaPedidoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponsePendencePedidoEtiquetagemItem) {
            mBinding.numeroPedidoApi.text = item.numeroPedido.toString()
            mBinding.quantidadeVolumes.text = item.quantidadeVolumes.toString()
            mBinding.quantidadePendente.text = item.quantidadePendente.toString()
            mBinding.normativaApi.text = item.tipoPedido
            if (item.tipoPedido == "Exportacao") mBinding.imageExportacao.setImageResource(R.drawable.ic_outline_rocket_launch_export24)
            else mBinding.imageExportacao.setImageResource(R.drawable.ic_baseline_west_norm24)
            if (item.numeroNormativa == null) {
                mBinding.txtNormativa.text = "Sem normativa"
                mBinding.numeroNormativaApi.text = ""
            } else {
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
