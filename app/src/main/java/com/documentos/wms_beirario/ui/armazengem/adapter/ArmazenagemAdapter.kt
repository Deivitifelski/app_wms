package com.documentos.wms_beirario.ui.armazengem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvArmazenagemBinding
import com.documentos.wms_beirario.model.armazenagem.ArmazenagemResponse


class ArmazenagemAdapter : RecyclerView.Adapter<ArmazenagemAdapter.ArmazenagemViewHolder>() {

    private var mListaArmazenagemResponse: MutableList<ArmazenagemResponse> = mutableListOf()

    inner class ArmazenagemViewHolder(val bindin: ItemRvArmazenagemBinding) :
        RecyclerView.ViewHolder(bindin.root) {

        fun geraItem(armazenagemResponseModel: ArmazenagemResponse) {
            with(bindin) {
                bindin.txtArmazenagem.text = armazenagemResponseModel.visualEnderecoOrigem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmazenagemViewHolder {
        val binding =
            ItemRvArmazenagemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArmazenagemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArmazenagemViewHolder, position: Int) {
        holder.geraItem(mListaArmazenagemResponse[position])
    }

    override fun getItemCount() = mListaArmazenagemResponse.size
    fun update(it: List<ArmazenagemResponse>) {
        mListaArmazenagemResponse.addAll(it)
        notifyDataSetChanged()
    }

    fun procurarDestino(qrCode: String): ArmazenagemResponse? {
        return mListaArmazenagemResponse.firstOrNull() {
            it.codigoBarrasEnderecoOrigem == qrCode
        }
    }


}