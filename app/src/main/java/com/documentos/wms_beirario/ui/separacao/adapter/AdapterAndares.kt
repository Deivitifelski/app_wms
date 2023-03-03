package com.documentos.wms_beirario.ui.separacao.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvEstanteSeparacaoBinding
import com.documentos.wms_beirario.model.separation.ResponseSeparation1

class AdapterAndares(
    private var onClick: (List<ResponseSeparation1>) -> Unit
) : RecyclerView.Adapter<AdapterAndares.SeparacaoItemViewHolder>() {

    var mListEstantesCheck = mutableListOf<String>()
    var mList = mutableListOf<ResponseSeparation1>()

    inner class SeparacaoItemViewHolder(val mBinding: ItemRvEstanteSeparacaoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(checks: ResponseSeparation1) {
            mBinding.itAndarSeparacao1.text = checks.andar
            mBinding.checkboxSeparacao1.isChecked = checks.status
            mBinding.quantidadePendenteApi.text = checks.quantidadePendente.toString()
            mBinding.quantidadeVolumesApi.text = checks.quantidadeVolumes.toString()

            mBinding.checkboxSeparacao1.setOnClickListener {
                if (mBinding.checkboxSeparacao1.isChecked) {
                    mListEstantesCheck.add(checks.andar)
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                    Log.e("CHECK TRUE ESTANTE||", "${checks.andar} POS: $layoutPosition")
                } else {
                    mListEstantesCheck.remove(checks.andar)
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                    Log.e("CHECK FALSE ESTANTE||", "${checks.andar} POS: $layoutPosition")
                }
            }
            itemView.setOnClickListener {
                if (!mBinding.checkboxSeparacao1.isChecked) {
                    mListEstantesCheck.add(checks.andar)
                    mBinding.checkboxSeparacao1.isChecked = true
                    mList[layoutPosition].status = true
                    onClick.invoke(mList)
                } else {
                    mBinding.checkboxSeparacao1.isChecked = false
                    mListEstantesCheck.remove(checks.andar)
                    mList[layoutPosition].status = false
                    onClick.invoke(mList)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeparacaoItemViewHolder {
        val mBinding =
            ItemRvEstanteSeparacaoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SeparacaoItemViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: SeparacaoItemViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = mList.size


    //PEGANDO A LISTA RECEBIDA DA TELA ANTERIOR -->
    fun setCkeckBox(andares: List<String>) {
        mListEstantesCheck = andares as MutableList<String>
        mList.forEach { estanteInit ->
            estanteInit.status = mListEstantesCheck.contains(estanteInit.andar)
        }
        notifyDataSetChanged()
    }

    fun selectAll() {
        try {
            try {
                mList.forEach { check ->
                    check.status = true
                }
                onClick.invoke(mList)
                notifyDataSetChanged()
            } catch (e: Exception) {
                Log.d("RV", "Erro ao fazer for no adapter!")
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun unSelectAll() {
        try {
            mList.forEach { check ->
                check.status = false
            }
            notifyDataSetChanged()
            onClick.invoke(mList)
        } catch (e: Exception) {
            Log.d("RV", "Erro ao fazer for no adapter!")
        }
    }

    fun update(itensCheckBox: List<ResponseSeparation1>) {
        mList.clear()
        mList.addAll(itensCheckBox)
        notifyDataSetChanged()
    }
}





