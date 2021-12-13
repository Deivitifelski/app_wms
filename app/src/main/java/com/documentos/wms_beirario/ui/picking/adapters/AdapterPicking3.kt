package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvPicking3Binding
import com.documentos.wms_beirario.model.picking.PickingResponse3

class AdapterPicking3(private val onClick: (PickingResponse3) -> Unit) :
    ListAdapter<PickingResponse3, AdapterPicking3.AdapterPickingViewHolder3>(DiffUtillPicking3()) {

    inner class AdapterPickingViewHolder3(val mBinding: ItemRvPicking3Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: PickingResponse3?) {
            mBinding.apiDescricaoDistribuicaoPicking3.text = item!!.descricaoDistribuicao
            mBinding.apiDescricaoEmbalagemPicking3.text = item.descricaoEmbalagem
            mBinding.apiQuantidadePicking3.text = item.quantidade.toString()
            mBinding.apiSkuPicking3.text = item.sku

            itemView.setOnClickListener {
                onClick.invoke(item)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPickingViewHolder3 {
        val mBinding =
            ItemRvPicking3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPickingViewHolder3(mBinding)
    }

    override fun onBindViewHolder(holder: AdapterPickingViewHolder3, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtillPicking3() : DiffUtil.ItemCallback<PickingResponse3>() {
    override fun areItemsTheSame(oldItem: PickingResponse3, newItem: PickingResponse3): Boolean {
        return oldItem.idProduto == newItem.idProduto
    }

    override fun areContentsTheSame(oldItem: PickingResponse3, newItem: PickingResponse3): Boolean {
        return oldItem == newItem
    }

}
