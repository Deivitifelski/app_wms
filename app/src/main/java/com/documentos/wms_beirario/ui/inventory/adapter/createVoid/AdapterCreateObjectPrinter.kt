package com.documentos.wms_beirario.ui.inventory.adapter.createVoid


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvCriaObjetoBinding
import com.documentos.wms_beirario.databinding.ItemRvDentroRvInventarioBinding
import com.documentos.wms_beirario.model.inventario.Combinacoes
import com.documentos.wms_beirario.model.inventario.Distribuicao


class AdapterCreateObjectPrinter(
    val context: Context,
    private val onClick: (Combinacoes, position: Int) -> Unit
) :
    RecyclerView.Adapter<AdapterCreateObjectPrinter.CriaItemObjetoViewHolder>() {

    var mList = mutableListOf<Combinacoes>()

    inner class CriaItemObjetoViewHolder(
        val mBinding: ItemRvCriaObjetoBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(dados: Combinacoes, position: Int) {
            mBinding.cabedalApi.setText(dados.cabedal.toString())
            mBinding.corApi.setText(dados.cor.toString())
            mBinding.linhaApi.setText(dados.linha.toString())
            mBinding.referenciaApi.setText(dados.referencia.toString())
            mBinding.qntparesApi.setText(dados.quantidadePares.toString())
            mBinding.corrugadoApi.setText(dados.corrugado.toString())
            mBinding.listObjetos.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mBinding.listObjetos.adapter = ListAdapter(dados.distribuicao)

            mBinding.deleteApi.setOnClickListener {
                onClick.invoke(dados, position)
            }
            /**EDITANDO COR TEXT -->*/
            mBinding.corApi.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    mList[layoutPosition].cor = 0
                    mBinding.corApi.setText("0")
                } else {
                    if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                        val txtEdit = newTxt.removeRange(0, 1)
                        mBinding.corApi.setText(txtEdit)
                        mList[layoutPosition].cor = newTxt.toString().toInt()
                    } else {
                        mList[layoutPosition].cor = newTxt.toString().toInt()
                    }
                    mBinding.corApi.setSelection(mBinding.corApi.length())
                }
            }
            /**EDITANDO CABEDAL TEXT -->*/
            mBinding.cabedalApi.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    mList[layoutPosition].cabedal = 0
                    mBinding.cabedalApi.setText("0")
                } else {
                    if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                        val txtEdit = newTxt.removeRange(0, 1)
                        mBinding.cabedalApi.setText(txtEdit)
                        mList[layoutPosition].cabedal = newTxt.toString().toInt()
                    } else {
                        mList[layoutPosition].cabedal = newTxt.toString().toInt()
                    }
                    mBinding.cabedalApi.setSelection(mBinding.cabedalApi.length())
                }
            }
            /**EDITANDO LINHA TEXT -->*/
            mBinding.linhaApi.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    mList[layoutPosition].linha = 0
                    mBinding.linhaApi.setText("0")
                } else {
                    if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                        val txtEdit = newTxt.removeRange(0, 1)
                        mBinding.linhaApi.setText(txtEdit)
                        mList[layoutPosition].linha = newTxt.toString().toInt()
                    } else {
                        mList[layoutPosition].linha = newTxt.toString().toInt()
                    }
                    mBinding.linhaApi.setSelection(mBinding.linhaApi.length())
                }
            }
            /**EDITANDO REFERENCIA TEXT -->*/
            mBinding.referenciaApi.doAfterTextChanged { newTxt ->
                if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                    mList[layoutPosition].referencia = 0
                    mBinding.referenciaApi.setText("0")
                } else {
                    if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                        val txtEdit = newTxt.removeRange(0, 1)
                        mBinding.referenciaApi.setText(txtEdit)
                        mList[layoutPosition].referencia = newTxt.toString().toInt()
                    } else {
                        mList[layoutPosition].referencia = newTxt.toString().toInt()
                    }
                    mBinding.referenciaApi.setSelection(mBinding.referenciaApi.length())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriaItemObjetoViewHolder {
        val bind = ItemRvCriaObjetoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CriaItemObjetoViewHolder(bind)
    }

    override fun onBindViewHolder(holder: CriaItemObjetoViewHolder, position: Int) {
        holder.bind(mList[position], position)
    }

    override fun getItemCount() = mList.size

    fun update(mCriaObjetoImprime: Combinacoes) {
        mList.add(mCriaObjetoImprime)
        notifyDataSetChanged()
    }

    fun retornaList() = mList.size

    fun retornaListPrinter() = mList


    fun updateCorrugado(quantidade: Int, corrugado: Int) {
        mList.map {
            it.quantidadePares = quantidade
            it.corrugado = corrugado
        }
        notifyDataSetChanged()
    }

    fun totalQnts(): Int {
        var qnts: Int = 0
        mList.map { list ->
            for (element in list.distribuicao) {
                qnts += element.quantidade
            }
        }
        return qnts
    }


    fun delete(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }


    /**---------------------------------ADAPTER RECYCLERVIEW INTERNA ----------------------------------*/

    inner class ListAdapter(listDistribuicao: List<Distribuicao>) :
        RecyclerView.Adapter<ListAdapter.ViewHolderListAdapter>() {
        private var mListObjetos = listDistribuicao

        inner class ViewHolderListAdapter(private val mmBinding: ItemRvDentroRvInventarioBinding) :
            RecyclerView.ViewHolder(mmBinding.root) {
            fun bind(it: Distribuicao) {
                with(mmBinding) {
                    tamanho.text = it.tamanho
                    quantidade.text = it.quantidade.toString()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderListAdapter {
            val bind = ItemRvDentroRvInventarioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolderListAdapter(bind)
        }

        override fun onBindViewHolder(holder: ViewHolderListAdapter, position: Int) {
            holder.bind(mListObjetos[position])
        }

        override fun getItemCount() = mListObjetos.size


    }


}






