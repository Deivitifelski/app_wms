package com.documentos.wms_beirario.ui.picking.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvPickingInit1Binding
import com.documentos.wms_beirario.model.picking.PickingResponseModel1

class PickingAdapter1(private var onClick: (PickingResponseModel1) -> Unit) :
    RecyclerView.Adapter<PickingAdapter1.PickingViewHolder1>() {

    private var mListPickingResponse1: MutableList<PickingResponseModel1> = mutableListOf()

    inner class PickingViewHolder1(val mBinding: ItemRvPickingInit1Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(pickingResponseModel1: PickingResponseModel1) {
            with(mBinding) {
                mBinding.nomeareaApi.text = pickingResponseModel1.nomeArea
            }
            itemView.setOnClickListener {
                onClick.invoke(pickingResponseModel1)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder1 {
        val mBinding =
            ItemRvPickingInit1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickingViewHolder1(mBinding)
    }

    override fun onBindViewHolder(holder: PickingViewHolder1, position: Int) {
        holder.bind(mListPickingResponse1[position])
    }

    override fun getItemCount() = mListPickingResponse1.size

    //Update adapter -->
    fun update(it: List<PickingResponseModel1>) {
        mListPickingResponse1.clear()
        mListPickingResponse1.addAll(it)
        notifyDataSetChanged()
    }
}