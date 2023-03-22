package com.documentos.wms_beirario.ui.documentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemDocumentationBinding
import com.documentos.wms_beirario.model.documentacao.ListImagens

class OnBoardAdapter(private val listimage: ListImagens) :
    RecyclerView.Adapter<OnBoardAdapter.OnBoardAdapterVh>() {


    inner class OnBoardAdapterVh(val item: ItemDocumentationBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(itens: Int) {
            item.imageDoc.setImageResource(itens)
            item.txtInf.text = "${listimage.namePage} - ${layoutPosition + 1}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardAdapterVh {
        val i = ItemDocumentationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardAdapterVh(i)
    }

    override fun onBindViewHolder(holder: OnBoardAdapterVh, position: Int) {
        holder.bind(listimage.list[position])
    }

    override fun getItemCount() = listimage.list.size


}