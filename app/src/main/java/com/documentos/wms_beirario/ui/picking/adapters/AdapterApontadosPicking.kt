package com.documentos.wms_beirario.ui.picking.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvNumSeriePicking2Binding
import com.documentos.wms_beirario.databinding.ItemRvPedidoPicking2Binding
import com.documentos.wms_beirario.model.picking.PickingResponseTest2
import com.documentos.wms_beirario.model.picking.PickingResponseTestList2


class AdapterApontadosPicking(val context: Context) :
    RecyclerView.Adapter<AdapterApontadosPicking.PickingViewHolder2>() {

    private var listDefault: MutableList<PickingResponseTest2> = mutableListOf()

    inner class PickingViewHolder2(val binding: ItemRvPedidoPicking2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(it: PickingResponseTest2) {
            with(binding) {
                binding.listObjetosPicking2.visibility = View.GONE
                binding.iconExpand.setImageResource(R.drawable.icon_expande_dow)
                binding.iconExpand.setOnClickListener {
                    if (binding.listObjetosPicking2.visibility == View.VISIBLE) {
                        binding.iconExpand.setImageResource(R.drawable.icon_expande_dow)
                        binding.listObjetosPicking2.visibility = View.GONE
                    } else {
                        binding.iconExpand.setImageResource(R.drawable.icon_expande_top)
                        binding.listObjetosPicking2.visibility = View.VISIBLE
                    }
                }
                apiPedidoPicking2.text = it.pedido
                apiEndVisualPicking2.text = it.enderecoVisualOrigem
                apiQtdPedidos.text = if (it.list.isNotEmpty()) it.list.size.toString() else "0"
                binding.listObjetosPicking2.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.listObjetosPicking2.adapter = ListAdapterInnerPicking2(it.list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder2 {
        val mBinding = ItemRvPedidoPicking2Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PickingViewHolder2(mBinding)
    }

    override fun onBindViewHolder(holder: PickingViewHolder2, position: Int) {
        holder.bind(listDefault[position])
    }

    override fun getItemCount() = listDefault.size

    //Update adapter -->
    fun update(listUpdate: List<PickingResponseTest2>) {
        listDefault.clear()
        if (listUpdate.isNotEmpty()) {
            listDefault.addAll(listUpdate)
        }
        notifyDataSetChanged()
    }

    /**---------------------------------ADAPTER RECYCLERVIEW INTERNA ----------------------------------*/

    inner class ListAdapterInnerPicking2(listDistribuicao: List<PickingResponseTestList2>) :
        RecyclerView.Adapter<ListAdapterInnerPicking2.ViewHolderListAdapterPicking2>() {
        private var mListObjetos = listDistribuicao

        inner class ViewHolderListAdapterPicking2(private val mmBinding: ItemRvNumSeriePicking2Binding) :
            RecyclerView.ViewHolder(mmBinding.root) {
            fun bind(it: PickingResponseTestList2) {
                with(mmBinding) {
                    apiNumeroDeSeriePicking2.text = it.numeroSerie
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolderListAdapterPicking2 {
            val bind = ItemRvNumSeriePicking2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolderListAdapterPicking2(bind)
        }

        override fun onBindViewHolder(holder: ViewHolderListAdapterPicking2, position: Int) {
            holder.bind(mListObjetos[position])
        }

        override fun getItemCount() = mListObjetos.size


    }

}