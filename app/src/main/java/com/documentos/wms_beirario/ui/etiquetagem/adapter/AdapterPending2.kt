package com.documentos.wms_beirario.ui.etiquetagem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemEtiquetagem2Binding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.model.etiquetagem.response.EtiquetagemResponse2

class AdapterPending2(val onclick: (EtiquetagemResponse2) -> Unit) :
    ListAdapter<EtiquetagemResponse2, AdapterPending2.AdapterPendingViewHolder2>(DiffUtilPending2()) {

    inner class AdapterPendingViewHolder2(val mBinding: ItemEtiquetagem2Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: EtiquetagemResponse2?) {
            mBinding.itEmissao.text = AppExtensions.formatData(item!!.dataEmissao.toString())
            mBinding.itFil.text = item.filial
            mBinding.itNf.text = item.numeroNotaFiscal.toString()
            mBinding.itPen.text = item.quantidadeVolumesPendentes.toString()
            mBinding.itSer.text = item.serieNotaFiscal
            mBinding.itVol.text = item.quantidadeVolumes.toString()
            //Click-->
            itemView.setOnClickListener {
                onclick.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPendingViewHolder2 {
        val binding =
            ItemEtiquetagem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPendingViewHolder2(binding)
    }

    override fun onBindViewHolder(holder: AdapterPendingViewHolder2, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilPending2() : DiffUtil.ItemCallback<EtiquetagemResponse2>() {
    override fun areItemsTheSame(
        oldItem: EtiquetagemResponse2,
        newItem: EtiquetagemResponse2
    ): Boolean {
        return oldItem.serieNotaFiscal == newItem.serieNotaFiscal
    }

    override fun areContentsTheSame(
        oldItem: EtiquetagemResponse2,
        newItem: EtiquetagemResponse2
    ): Boolean {
        return oldItem == newItem
    }

}
