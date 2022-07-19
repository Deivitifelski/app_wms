package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria3Binding
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaDistribuicaoFinishBinding
import com.documentos.wms_beirario.model.auditoria.Distribuicao
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoria
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoriaItem


class AuditoriaAdapter3 : RecyclerView.Adapter<AuditoriaAdapter3.AuditoriaAdapterVH3>() {

    private var mList = mutableListOf<ResponseFinishAuditoriaItem>()
    private var mListAll = mutableListOf<ResponseFinishAuditoriaItem>()

    inner class AuditoriaAdapterVH3(val mBinding: ItemRvAuditoria3Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseFinishAuditoriaItem) {
            /**SE A AUDITORIA FOR (FALSE) DEVE MOSTRAR OS ITENS POIS AINDA ESTÃO PENDENTES -->*/
            with(mBinding) {
                //COMO SKU PODE SER ENORME E QUEBRAR O APP,SÓ FOI CRIADO ESSA VERIFICAÇÃO PARA DIMINUIR POUCO -->
                if (item.sku.length > 22) {
                    skuApi.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                }
                qntApi.text = item.quantidade.toString()
                endVisualApi.text = item.enderecoVisual
                skuApi.text = item.sku
                apiGrade.text = item.codigoGrade
                rvAuditoria03.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = AdapterInneruditoria(mList[layoutPosition].distribuicao)
                }
                mBinding.editQnt.setText(item.quantidade.toString())
            }


            /**EDITANDO MANUALMENTE A QUANTIDADE -->*/
            mBinding.editQnt.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    mList[layoutPosition].quantidade = 0
                    mBinding.editQnt.setText("0")
                } else {
                    if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                        val txtEdit = newTxt.removeRange(0, 1)
                        mBinding.editQnt.setText(txtEdit)
                        mList[layoutPosition].quantidade = newTxt.toString().toInt()
                    } else {
                        mList[layoutPosition].quantidade = newTxt.toString().toInt()
                    }
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                }
                Log.d(
                    "EDITOU",
                    "ID[${mList[layoutPosition].id}] || VALOR[${mList[layoutPosition].quantidade}]"
                )
            }
            /**BUTTON SIMPLES ADD ++ -->*/
            mBinding.buttonAddAuditoria.setOnClickListener {
                mList[layoutPosition].quantidade += 1
                mBinding.editQnt.setText(mList[layoutPosition].quantidade.toString())
                mBinding.editQnt.setSelection(mBinding.editQnt.length())
                Log.d(
                    "CLICOU ADD ++ ",
                    "ID[${mList[layoutPosition].id}] || VALOR[${mList[layoutPosition].quantidade}]"
                )
            }
            /**BUTTON SIMPLES REMOVER -->*/
            mBinding.buttonRemoveAuditoria.setOnClickListener {
                mList[layoutPosition].quantidade -= 1
                val text = mList[layoutPosition].quantidade
                if (text <= 0) {
                    mList[layoutPosition].quantidade = 0
                    mBinding.editQnt.setText("0")
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                } else {
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                    mList[layoutPosition].quantidade = text
                    mBinding.editQnt.setText(text.toString())
                }
                Log.d(
                    "CLICOU  REMOVER -- ",
                    "ID[${mList[layoutPosition].id}] || VALOR[${mList[layoutPosition].quantidade}]"
                )
            }
        }
    }

    /**RETORNA O OBJETO DA LISTA TOTAL ONDE O COD BIPADO CONTENHA NO MESMO --> */
    fun returnCodBarras(codigoBipado: String): ResponseFinishAuditoriaItem? {
        return mListAll.firstOrNull {
            it.codBarrasEndereco == codigoBipado
        }
    }

    /**GERAR ALGO QUE POSSA VALIDAR A DFERENÇA ENTRE AS LISTAS PARA ATUAIZR SEM MEXER NO QUE JA FOI UPDATE -->*/
    fun updateList(list: ResponseFinishAuditoria) {
        //CRIADO 2 LISTAS UMA COM TODOS OS ITENS PARA VERIFICAR SE JA FOI AUDITADO POR ELA (mListALl)-->
        mList.clear()
        mListAll.clear()
        list.sortBy { it.enderecoVisual }
        mListAll.addAll(list)
        list.forEach { list ->
            if (!list.auditado) {
                mList.add(list)
            }
        }
        Log.e("UPDATE", "updateList:  $mList")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapterVH3 {
        val binding =
            ItemRvAuditoria3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapterVH3(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapterVH3, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size
}

/**
 * RECYCLERVIEW INTERNA -->
 */
class AdapterInneruditoria(distribuicao: List<Distribuicao>) :
    RecyclerView.Adapter<AdapterInneruditoria.AdapterInneruditoriaVh>() {
    private var mListObjetos = distribuicao

    inner class AdapterInneruditoriaVh(private val binding: ItemRvAuditoriaDistribuicaoFinishBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInner(distribuicao: Distribuicao) {
            binding.numeroCalAdoApi.text = distribuicao.tamanho
            binding.quantidadeParesApi.text = distribuicao.quantidade.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterInneruditoriaVh {
        val mBInding = ItemRvAuditoriaDistribuicaoFinishBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterInneruditoriaVh(mBInding)
    }

    override fun onBindViewHolder(holder: AdapterInneruditoriaVh, position: Int) {
        holder.bindInner(mListObjetos[position])
    }

    override fun getItemCount() = mListObjetos.size
}
