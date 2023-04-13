package com.documentos.wms_beirario.ui.boardingConference.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemApprovedBoardingBinding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding

class AdapterConferenceBoardingAdapter() :
    RecyclerView.Adapter<AdapterConferenceBoardingAdapter.AdapterConferenceBoardingAdapterVh>() {

    private var mList = mutableListOf<DataResponseBoarding>()

    inner class AdapterConferenceBoardingAdapterVh(val binding: ItemApprovedBoardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataResponseBoarding) {
            with(binding) {
                skuApi.text = item.sku
                qntApi.text = item.quantidade.toString()
                eanApi.text = item.ean
                sequencialApiApi.text = item.sequencial.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterConferenceBoardingAdapterVh {
        val i =
            ItemApprovedBoardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterConferenceBoardingAdapterVh(i)
    }

    override fun onBindViewHolder(holder: AdapterConferenceBoardingAdapterVh, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun lookForObject(qrCode: String): DataResponseBoarding? {
        return mList.firstOrNull() {
            it.numeroSerie == qrCode
        }
    }

    fun update(listAproved: MutableList<DataResponseBoarding>) {
        mList.clear()
        listAproved.sortBy { it.sequencial }
        mList.addAll(listAproved)
        notifyDataSetChanged()
    }
}

