package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvNumSeriePicking2Binding
import com.documentos.wms_beirario.model.picking.PickingResponseNewFluxo

class AdapterPickingNewFluxo(private var onClick: (PickingResponseNewFluxo) -> Unit) :
    RecyclerView.Adapter<AdapterPickingNewFluxo.PickingViewHolder1>() {

    private var mListPickingResponseNewFluxo: MutableList<PickingResponseNewFluxo> = mutableListOf()

    inner class PickingViewHolder1(val mBinding: ItemRvNumSeriePicking2Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(pickingResponseModelNewFluxo: PickingResponseNewFluxo) {
            with(mBinding) {
                mBinding.apiNumeroDeSeriePicking2.text = pickingResponseModelNewFluxo.nomeArea
            }
            itemView.setOnClickListener {
                onClick.invoke(pickingResponseModelNewFluxo)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder1 {
        val mBinding =
            ItemRvNumSeriePicking2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PickingViewHolder1(mBinding)
    }

    override fun onBindViewHolder(holder: PickingViewHolder1, position: Int) {
        holder.bind(mListPickingResponseNewFluxo[position])
    }

    override fun getItemCount() = mListPickingResponseNewFluxo.size

    //Update adapter -->
    fun update(it: List<PickingResponseNewFluxo>) {
        mListPickingResponseNewFluxo.clear()
        mListPickingResponseNewFluxo.addAll(it)
        notifyDataSetChanged()
    }


}