package com.documentos.wms_beirario.ui.inventario.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvCorrugadoParesSelectBinding
import com.documentos.wms_beirario.model.inventario.Distribuicao
import com.documentos.wms_beirario.ui.inventario.fragment.createVoid.AddVoidFragment
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom

class AdapterCreateVoidItem(private val onClick: (Distribuicao, position: Int) -> Unit) :
    RecyclerView.Adapter<AdapterCreateVoidItem.AdapterCreateVoidItemViewHolder>() {

    var mList = mutableListOf<Distribuicao>()

    inner class AdapterCreateVoidItemViewHolder(val mBinding: ItemRvCorrugadoParesSelectBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(itemClicked: Distribuicao) {
            mBinding.numeroCalAdoApi.text = itemClicked.tamanho
            mBinding.quantidadeParesApi.text = itemClicked.quantidade.toString()
            itemView.setOnClickListener {
                onClick.invoke(itemClicked, position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCreateVoidItemViewHolder {
        val binding = ItemRvCorrugadoParesSelectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterCreateVoidItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterCreateVoidItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size


    fun updateCreateVoid(
        mQntCorrugadoTotal: Int,
        mQntShoesObject: Int,
        fragment: AddVoidFragment,
        context: Context,
        mObjetoCreate: Distribuicao,
        mPosition: Int?
    ) {
        when {
            mPosition != null -> {
//                mList.removeAt(position)
                if (mQntShoesObject + mObjetoCreate.quantidade - mList[mPosition].quantidade > mQntCorrugadoTotal) {
                    CustomAlertDialogCustom().alertMessageErrorSimples(
                        context,
                        "Limite excedido do corrugado!"
                    )
                } else {
                    mList.removeAt(mPosition)
                    mList.add(mPosition, mObjetoCreate)
                }
            }
            /**FALTA O CLICK NA RECYCLERVIEW DAR CERTO A CONTAGEM---------------->*/
            mList.any { it.tamanho == mObjetoCreate.tamanho } -> {
                val mQnt01 = returnQnts(mObjetoCreate.tamanho)
                val mQnt02 = mQnt01!!.quantidade

                if (mQntShoesObject + mObjetoCreate.quantidade - mQnt02 > mQntCorrugadoTotal) {
                    CustomAlertDialogCustom().alertMessageErrorSimples(
                        context,
                        "Limite excedido do corrugado!"
                    )
                } else {
                    for (i in 0..10) {
                        mList.remove(Distribuicao(mObjetoCreate.tamanho, i))
                    }
                    mList.add(mObjetoCreate)
                }

            }
            else -> {
                //Quando o primeiro item e adicionado -->
                if (mQntShoesObject + mObjetoCreate.quantidade > mQntCorrugadoTotal) CustomAlertDialogCustom().alertMessageErrorSimples(
                    context,
                    "Limite excedido do corrugado!"
                ) else
                    mList.add(mObjetoCreate)
            }
        }
        mList.sortBy { it.tamanho }
        if (mPosition != null) {
            this.notifyItemChanged(mPosition)
        } else {
            notifyDataSetChanged()
        }
    }

    fun returList() = mList

    fun returnQnts(mTam: String): Distribuicao? {
        return mList.firstOrNull() {
            it.tamanho == mTam
        }
    }

    //RETORNA A QUANTIDADE JA ADICIONADA-->
    fun getQntShoesObject(): Int {
        var qntShoes: Int = 0
        mList.map {
            qntShoes += it.quantidade
        }
        return qntShoes
    }

    fun deleteObject(tamShoes: Int) {
        for (i in 0..10) {
            mList.remove(Distribuicao(tamShoes.toString(), i))
        }
        notifyDataSetChanged()
    }

    fun clearList() {
        mList.clear()
        notifyDataSetChanged()
    }

}

