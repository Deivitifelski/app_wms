package com.documentos.wms_beirario.ui.boardingConference.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemApprovedBoardingBinding
import com.documentos.wms_beirario.databinding.ItemNotApprovedBoardingBinding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding

class AdapterNotConferenceBoardingAdapter() :
    ListAdapter<DataResponseBoarding, AdapterNotConferenceBoardingAdapter.AdapterNotConferenceBoardingAdapterVh>(
        NotAProveddBoarding()
    ) {

    inner class AdapterNotConferenceBoardingAdapterVh(val binding: ItemNotApprovedBoardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataResponseBoarding) {
            with(binding) {
                skuApi.text = item.sku
                qntApi.text = item.quantidade.toString()
                sequencialApiApi.text = item.sequencial.toString()
            }

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterNotConferenceBoardingAdapterVh {
        val i =
            ItemNotApprovedBoardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AdapterNotConferenceBoardingAdapterVh(i)
    }

    override fun onBindViewHolder(holder: AdapterNotConferenceBoardingAdapterVh, position: Int) {
        holder.bind(getItem(position))
    }
}

class NotAProveddBoarding() : DiffUtil.ItemCallback<DataResponseBoarding>() {
    override fun areItemsTheSame(
        oldItem: DataResponseBoarding,
        newItem: DataResponseBoarding
    ): Boolean {
        return oldItem.sku == newItem.sku && oldItem.idEnderecoOrigem == newItem.idEnderecoOrigem && oldItem.sequencial == newItem.sequencial
    }

    override fun areContentsTheSame(
        oldItem: DataResponseBoarding,
        newItem: DataResponseBoarding
    ): Boolean {
        return oldItem == newItem
    }

}
