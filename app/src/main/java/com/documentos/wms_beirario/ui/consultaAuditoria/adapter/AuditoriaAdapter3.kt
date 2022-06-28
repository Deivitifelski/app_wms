package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria3Binding
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria3
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaItem3


class AuditoriaAdapter3 : RecyclerView.Adapter<AuditoriaAdapter3.AuditoriaAdapterVH3>() {

    private var mOldList = mutableListOf<ResponseAuditoriaItem3>()

    inner class AuditoriaAdapterVH3(val mBinding: ItemRvAuditoria3Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseAuditoriaItem3) {
            /**SE A AUDITORIA FOR (FALSE) DEVE MOSTRAR OS ITENS POIS AINDA ESTÃO PENDENTES -->*/
            with(mBinding) {
                if (!item.auditado) {
                    qntApi.text = item.quantidade.toString()
                    endVisualApi.text = item.enderecoVisual
                    skuApi.text = item.sku
                    mBinding.editQnt.setText(item.quantidade.toString())
                }
            }

            /**EDITANDO MANUALMENTE A QUANTIDADE -->*/
            mBinding.editQnt.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    mOldList[layoutPosition].quantidade = 0
                    mBinding.editQnt.setText("0")
                } else {
                    if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                        val txtEdit = newTxt.removeRange(0, 1)
                        mBinding.editQnt.setText(txtEdit)
                        mOldList[layoutPosition].quantidade = newTxt.toString().toInt()
                    } else {
                        mOldList[layoutPosition].quantidade = newTxt.toString().toInt()
                    }
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                }
                Log.d(
                    "EDITOU",
                    "ID[${mOldList[layoutPosition].id}] || VALOR[${mOldList[layoutPosition].quantidade}]"
                )
            }
            /**BUTTON SIMPLES ADD ++ -->*/
            mBinding.buttonAddAuditoria.setOnClickListener {
                mOldList[layoutPosition].quantidade += 1
                mBinding.editQnt.setText(mOldList[layoutPosition].quantidade.toString())
                mBinding.editQnt.setSelection(mBinding.editQnt.length())
                Log.d(
                    "CLICOU ADD ++ ",
                    "ID[${mOldList[layoutPosition].id}] || VALOR[${mOldList[layoutPosition].quantidade}]"
                )
            }
            /**BUTTON SIMPLES REMOVER -->*/
            mBinding.buttonRemoveAuditoria.setOnClickListener {
                mOldList[layoutPosition].quantidade -= 1
                val text = mOldList[layoutPosition].quantidade
                if (text <= 0) {
                    mOldList[layoutPosition].quantidade = 0
                    mBinding.editQnt.setText("0")
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                } else {
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                    mOldList[layoutPosition].quantidade = text
                    mBinding.editQnt.setText(text.toString())
                }
                Log.d(
                    "CLICOU  REMOVER -- ",
                    "ID[${mOldList[layoutPosition].id}] || VALOR[${mOldList[layoutPosition].quantidade}]"
                )
            }
        }
    }

    /**RETORNA O OBJETO DA LISTA ONDE O COD BIPADO CONTENHA NO MESMO --> */
    fun returnCodBarras(codigoBipado: String): ResponseAuditoriaItem3? {
        return mOldList.firstOrNull {
            it.codBarrasEndereco == codigoBipado
        }
    }

    /**GERAR ALGO QUE POSSA VALIDAR A DFERENÇA ENTRE AS LISTAS PARA ATUAIZR SEM MEXER NO QUE JA FOI UPDATE -->*/
    fun updateList(list: ResponseAuditoria3) {
        mOldList.clear()
        mOldList.addAll(list)
        notifyDataSetChanged()
    }

    /**FAZ UPDATE DA LISTA PELO DIFFUTIL CALLBACK/COMPARANDO A DIFERENÇA -->*/
    fun updateListDiffUtil(mNewList: List<ResponseAuditoriaItem3>) {
        val diffUtil = AuditoriaDiffUtill(mOldList, mNewList)
        val diffList = DiffUtil.calculateDiff(diffUtil)
        mOldList = mNewList as MutableList<ResponseAuditoriaItem3>
        diffList.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditoriaAdapterVH3 {
        val binding =
            ItemRvAuditoria3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditoriaAdapterVH3(binding)
    }

    override fun onBindViewHolder(holder: AuditoriaAdapterVH3, position: Int) {
        holder.bind(mOldList[position])
    }

    override fun getItemCount() = mOldList.size
}

class AuditoriaDiffUtill(
    private val oldList: List<ResponseAuditoriaItem3>,
    private val newList: List<ResponseAuditoriaItem3>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> {
                false
            }
            oldList[oldItemPosition].quantidade != newList[newItemPosition].quantidade -> {
                false
            }
            else -> true
        }

    }

}
