package com.documentos.wms_beirario.ui.boardingConference.adapter


import android.util.Log
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
                try {
                    numeroSerieOrEan(item)
                    skuApi.text = item.sku
                    qntApi.text = item.quantidade.toString()
                    pedidoApiApi.text = if (item.pedido.isNullOrEmpty()) "-" else item.pedido
                    sequencialApiApi.text = item.sequencial.toString()
                } catch (e: Exception) {
                    Log.e("AdapterNotConferenceBoardingAdapter -->", "Erro adapter!")
                }
            }
        }

        private fun ItemApprovedBoardingBinding.numeroSerieOrEan(item: DataResponseBoarding) {
            try {
                if (item.numeroSerie.isNullOrEmpty()) {
                    numSerieTxt.text = "Ean"
                    numSerieApi.text = item.ean
                } else {
                    numSerieTxt.text = "N°.Série"
                    numSerieApi.text = item.numeroSerie
                }
            } catch (e: Exception) {
                numSerieTxt.text = "N°.Série"
                numSerieApi.text = "-"
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

    fun lookForNumSerieObject(
        qrCode: String,
        listPending: MutableList<DataResponseBoarding>
    ): DataResponseBoarding? {
        return listPending.firstOrNull() {
            it.numeroSerie == qrCode
        }
    }

    fun update(listAproved: MutableList<DataResponseBoarding>) {
        mList.clear()
        listAproved.sortBy { it.sequencial }
        mList.addAll(listAproved)
        notifyDataSetChanged()
    }


    fun contaisQrCode(qrCode: String): Boolean {
        val i = mList.filter { it.numeroSerie == qrCode }
        return i.isNotEmpty()
    }
}

