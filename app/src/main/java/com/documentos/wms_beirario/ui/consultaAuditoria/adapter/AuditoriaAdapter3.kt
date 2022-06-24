package com.documentos.wms_beirario.ui.consultaAuditoria.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvAuditoria3Binding
import com.documentos.wms_beirario.databinding.ItemRvAuditoriaFinishBinding

import com.documentos.wms_beirario.model.auditoria.ResponseAuditoria3
import com.documentos.wms_beirario.model.auditoria.ResponseAuditoriaItem3
import com.documentos.wms_beirario.ui.consultaAuditoria.AuditoriaActivity2
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomSnackBarCustom

class AuditoriaAdapter3(private val context: AuditoriaActivity2) :
    RecyclerView.Adapter<AuditoriaAdapter3.AuditoriaAdapterVH3>() {


    private val mList = mutableListOf<ResponseAuditoriaItem3>()

    inner class AuditoriaAdapterVH3(val mBinding: ItemRvAuditoria3Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseAuditoriaItem3) {
            with(mBinding) {
                qntApi.text = item.quantidade.toString()
                endVisualApi.text = item.enderecoVisual
                skuApi.text = item.sku
                mBinding.editQnt.setText(item.quantidade.toString())
            }

            mBinding.editQnt.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    CustomAlertDialogCustom().vibrar(context)
                    CustomSnackBarCustom().toastCustomError(
                        context = context,
                        "Quantidade não pode ser menor que zero!"
                    )
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
            /**BUTTON ADD ++ -->*/
            mBinding.buttonAddAuditoria.setOnClickListener {
                mList[layoutPosition].quantidade += 1
                mBinding.editQnt.setText(mList[layoutPosition].quantidade.toString())
                mBinding.editQnt.setSelection(mBinding.editQnt.length())
                Log.d(
                    "CLICOU ADD ++ ",
                    "ID[${mList[layoutPosition].id}] || VALOR[${mList[layoutPosition].quantidade}]"
                )
            }
            /**BUTTON REMOVER -->*/
            mBinding.buttonRemoveAuditoria.setOnClickListener {
                mList[layoutPosition].quantidade -= 1
                val text = mList[layoutPosition].quantidade
                if (text <= 0) {
                    mList[layoutPosition].quantidade = 0
                    mBinding.editQnt.setText("0")
                    mBinding.editQnt.setSelection(mBinding.editQnt.length())
                    CustomAlertDialogCustom().vibrar(context)
                    CustomSnackBarCustom().toastCustomError(
                        context = context,
                        "Quantidade não pode ser menor que zero!"
                    )
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

    /**RETORNA O OBJETO DA LISTA ONDE O COD BIPADO CONTENHA NO MESMO --> */
    fun returnCodBarras(codigoBipado: String): ResponseAuditoriaItem3? {
        return mList.firstOrNull {
            it.codBarrasEndereco == codigoBipado
        }
    }

    fun updateList(list: ResponseAuditoria3) {
        mList.clear()
        mList.addAll(list)
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
