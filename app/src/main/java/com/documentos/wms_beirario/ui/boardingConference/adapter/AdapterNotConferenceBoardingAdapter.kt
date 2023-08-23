package com.documentos.wms_beirario.ui.boardingConference.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemNotApprovedBoardingBinding
import com.documentos.wms_beirario.model.conferenceBoarding.DataResponseBoarding

class AdapterNotConferenceBoardingAdapter() :
    RecyclerView.Adapter<AdapterNotConferenceBoardingAdapter.AdapterNotConferenceBoardingAdapterVh>() {

    private var mList = mutableListOf<DataResponseBoarding>()

    inner class AdapterNotConferenceBoardingAdapterVh(val binding: ItemNotApprovedBoardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataResponseBoarding) {
            with(binding) {
                try {
                    skuApi.text = item.sku
                    qntApi.text = item.quantidade.toString()
                    numSerieApi.text =
                        if (item.numeroSerie.isNullOrEmpty()) "-" else item.numeroSerie
                    pedidoApiApi.text = if (item.pedido.isNullOrEmpty()) "-" else item.pedido
                    sequencialApiApi.text = item.sequencial.toString()
                } catch (e: Exception) {
                    Log.e("AdapterNotConferenceBoardingAdapter -->", "Erro adapter!")
                }
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
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun lookForObject(
        qrCode: String,
        listAproved: MutableList<DataResponseBoarding>
    ): DataResponseBoarding? {
        return listAproved.firstOrNull() {
            it.numeroSerie == qrCode || it.ean == qrCode
        }
    }

    fun update(listNotAproved: MutableList<DataResponseBoarding>) {
        mList.clear()
        listNotAproved.sortBy { it.sequencial }
        mList.addAll(listNotAproved)
        notifyDataSetChanged()
    }
}

