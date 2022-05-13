package com.documentos.wms_beirario.ui.consultacodbarras.adapter

import VolumesModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvVolumesClickBinding


class CodBarrasVolumeClickAdapter :
    RecyclerView.Adapter<CodBarrasVolumeClickAdapter.VolumeClickViewHolder>() {

    private val mLIstVolumeClick = mutableListOf<VolumesModel>()

    inner class VolumeClickViewHolder(val mBInding: ItemRvVolumesClickBinding) :
        RecyclerView.ViewHolder(mBInding.root) {
        fun geraItem(it: VolumesModel) {
            with(mBInding) {
                itNomeVolumeClick.text = it.nome
                itCodDistribuicaoVolumeClick.text = it.codigoDistribuicao.toString()
                itCodEmbalagemVolumeClick.text = it.codigoEmbalagem.toString()
                itDesDistribuicaoVolumeClick.text = it.descricaoDistribuicao
                itDescEmbalagemVolumeClick.text = it.descricaoEmbalagem
                itSkuVolumeClick.text = it.sku
                itQuantidadeVolumeClick.text = it.quantidade.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolumeClickViewHolder {
        val mBInding =
            ItemRvVolumesClickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolumeClickViewHolder(mBInding)
    }

    override fun onBindViewHolder(holder: VolumeClickViewHolder, position: Int) {
        holder.geraItem(mLIstVolumeClick[position])
    }


    override fun getItemCount() = mLIstVolumeClick.size

    //Update recyclerView -->
    fun update(it: List<VolumesModel>) {
        this.mLIstVolumeClick.addAll(it)
        notifyDataSetChanged()
    }

    //Fazendo busca do QrCode Lido -->
//    fun procurarDestino(qrCode2: String): com.documentos.wms_beirario.model.codBarras.VolumeModelCB? {
//        return mCodigodeBarrasLIstVolume.firstOrNull() {
//            it.numeroSerie == qrCode2
//        }
//    }


}
