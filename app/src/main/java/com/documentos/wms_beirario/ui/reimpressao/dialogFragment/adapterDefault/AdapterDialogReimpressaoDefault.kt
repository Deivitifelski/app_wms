package com.documentos.wms_beirario.ui.reimpressao.dialogFragment.adapterDefault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvDialogReimpressaoNumSerieBinding
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressao
import com.documentos.wms_beirario.model.reimpressao.ResponseEtiquetasReimpressaoItem

class AdapterDialogReimpressaoDefault(val onClick: (ResponseEtiquetasReimpressaoItem) -> Unit) :
    RecyclerView.Adapter<AdapterDialogReimpressaoDefault.AdapterDialogNumSerieVH>() {

    private var mList: MutableList<ResponseEtiquetasReimpressaoItem> = mutableListOf()

    inner class AdapterDialogNumSerieVH(val mBinding: ItemRvDialogReimpressaoNumSerieBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseEtiquetasReimpressaoItem) {
            mBinding.txtTipoApi.text = item.descricaoEtiqueta

            mBinding.buttonImprimir.setOnClickListener {
                onClick.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDialogNumSerieVH {
        val bind = ItemRvDialogReimpressaoNumSerieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterDialogNumSerieVH(bind)
    }

    override fun onBindViewHolder(holder: AdapterDialogNumSerieVH, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    fun update(itemClick: ResponseEtiquetasReimpressao) {
        mList.clear()
        mList.addAll(itemClick)
        notifyDataSetChanged()
    }
}