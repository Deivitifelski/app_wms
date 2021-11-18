package com.documentos.wms_beirario.ui.consultacodbarras.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvVolumeDistribuicaoCodbarrasBinding
import com.example.coletorwms.model.codBarras.DistribuicaoModel


class CodBarrasVolumeAdapter :
    RecyclerView.Adapter<CodBarrasVolumeAdapter.consultacodigoViewHolder>() {

    private val mDistribuicaoLIst = mutableListOf<DistribuicaoModel>()

    inner class consultacodigoViewHolder(val mBinding: ItemRvVolumeDistribuicaoCodbarrasBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(volumeModelCB: DistribuicaoModel) {
            with(mBinding) {
                ean.text = volumeModelCB.ean
                txtSku.text = volumeModelCB.sku.toString()
                txtTamanho.text = volumeModelCB.tamanho.toString()
                txtQuantidade.text = volumeModelCB.quantidade.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): consultacodigoViewHolder {
        val mBinding = ItemRvVolumeDistribuicaoCodbarrasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return consultacodigoViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: consultacodigoViewHolder, position: Int) {
        holder.geraItem(mDistribuicaoLIst[position])
    }


    override fun getItemCount() = mDistribuicaoLIst.size

    //Update recyclerView -->
    fun update(it: List<DistribuicaoModel>) {
        this.mDistribuicaoLIst.addAll(it)
        notifyDataSetChanged()
    }

    //Fazendo busca do QrCode Lido -->
//    fun procurarDestino(qrCode2: String): VolumeModelCB? {
//        return mCodigodeBarrasLIstVolume.firstOrNull() {
//            it.numeroSerie == qrCode2
//        }
//    }


}
