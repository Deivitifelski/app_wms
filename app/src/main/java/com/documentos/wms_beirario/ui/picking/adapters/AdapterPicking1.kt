package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvPicking1Binding
import com.documentos.wms_beirario.model.picking.PickingResponse1

class AdapterPicking1(private var onClick: (PickingResponse1) -> Unit) :
    RecyclerView.Adapter<AdapterPicking1.PickingViewHolder1>() {

    private var mListPickingResponse1: MutableList<PickingResponse1> = mutableListOf()

    inner class PickingViewHolder1(val mBinding: ItemRvPicking1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(pickingResponseModel1: PickingResponse1) {
            with(mBinding) {
                mBinding.apiNumeroDeSeriePicking2.text = pickingResponseModel1.nomeArea
            }
            itemView.setOnClickListener {
                onClick.invoke(pickingResponseModel1)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder1 {
        val mBinding =
            ItemRvPicking1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickingViewHolder1(mBinding)
    }

    override fun onBindViewHolder(holder: PickingViewHolder1, position: Int) {
        holder.bind(mListPickingResponse1[position])
    }

    override fun getItemCount() = mListPickingResponse1.size

    //Update adapter -->
    fun update(it: List<PickingResponse1>) {
        mListPickingResponse1.clear()
        mListPickingResponse1.addAll(it)
        notifyDataSetChanged()
    }


}