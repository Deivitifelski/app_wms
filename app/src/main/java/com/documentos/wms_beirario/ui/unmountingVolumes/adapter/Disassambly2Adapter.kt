package com.documentos.wms_beirario.ui.unmountingVolumes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaDistribuicaoFinishBinding
import com.documentos.wms_beirario.databinding.ItemRvUnmountingInnerCardBinding
import com.documentos.wms_beirario.databinding.ItemRvUnmountingVolumes2Binding
import com.documentos.wms_beirario.model.desmontagemVol.Distribuicao
import com.documentos.wms_beirario.model.desmontagemVol.ResponseUnMountingFinishItem


class Disassambly2Adapter() :
    RecyclerView.Adapter<Disassambly2Adapter.Disassambly2AdapterViewHolder>() {

    private val TAG = "ADAPTER"
    private var mListaTaskDisassambly1: MutableList<ResponseUnMountingFinishItem> = mutableListOf()

    inner class Disassambly2AdapterViewHolder(val bindin: ItemRvUnmountingVolumes2Binding) :
        RecyclerView.ViewHolder(bindin.root) {

        fun geraItem(item: ResponseUnMountingFinishItem) {
            with(bindin) {
                enderecoVisualApi.text = item.nome
                qntApi.text = item.quantidadeVolumes.toString()
                rvInnerDistruibuicaoUnmonting2.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = AdapterInnerUnMonting(item.distribuicao)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Disassambly2AdapterViewHolder {
        val binding =
            ItemRvUnmountingVolumes2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Disassambly2AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Disassambly2AdapterViewHolder, position: Int) {
        holder.geraItem(mListaTaskDisassambly1[position])
    }

    override fun getItemCount() = mListaTaskDisassambly1.size
    fun update(listPendent: List<ResponseUnMountingFinishItem>) {
        mListaTaskDisassambly1.clear()
        mListaTaskDisassambly1.addAll(listPendent)
        notifyDataSetChanged()
    }

}

class AdapterInnerUnMonting(distribuicao: List<Distribuicao>) :
    RecyclerView.Adapter<AdapterInnerUnMonting.AdapterInnerUnMontingVh>() {

    private val list = distribuicao

    inner class AdapterInnerUnMontingVh(val binding: ItemRvUnmountingInnerCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInner(distribuicaoInner: Distribuicao) {
            with(binding) {
                quantidadeParesApi.text = distribuicaoInner.quantidade.toString()
                numeroCalAdoApi.text = distribuicaoInner.tamanho
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInnerUnMontingVh {
        val binding = ItemRvUnmountingInnerCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterInnerUnMontingVh(binding)
    }

    override fun onBindViewHolder(holder: AdapterInnerUnMontingVh, position: Int) {
        holder.bindInner(list[position])
    }

    override fun getItemCount() = list.size

}
