package com.documentos.wms_beirario.ui.documentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemDocumentationBinding

class OnBoardAdapter(private val listimage: List<Int>) :
    RecyclerView.Adapter<OnBoardAdapter.OnBoardAdapterVh>() {


    inner class OnBoardAdapterVh(val item: ItemDocumentationBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(itens: Int) {
            item.imageDoc.setImageResource(itens)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardAdapterVh {
        val i = ItemDocumentationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardAdapterVh(i)
    }

    override fun onBindViewHolder(holder: OnBoardAdapterVh, position: Int) {
        holder.bind(listimage[position])
    }

    override fun getItemCount() = listimage.size


}