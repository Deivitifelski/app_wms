package com.documentos.wms_beirario.ui.consultacodbarras.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvVolumesClickBinding
import com.documentos.wms_beirario.databinding.ItemRvVolumesInnerBinding
import com.documentos.wms_beirario.model.codBarras.NumSerieVolModel
import com.documentos.wms_beirario.model.codBarras.VolumesModel


class CodBarrasVolumeClickAdapter(val context: Context) :
    RecyclerView.Adapter<CodBarrasVolumeClickAdapter.VolumeClickViewHolder>() {

    private val mLIstVolumeClick = mutableListOf<VolumesModel>()

    inner class VolumeClickViewHolder(val mBinding: ItemRvVolumesClickBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun geraItem(listItem: VolumesModel) {
            with(mBinding) {
                itCodDistribuicaoVolumeClick.text = listItem.codigoDistribuicao.toString()
                itSkuVolumeClick.text = listItem.sku
                itQuantidadeVolumeClick.text = listItem.quantidade.toString()
            }

            /**
             *CLIQUE NO BUTTON PARA EXPANDIR A RECYCLERVIEW DENTRO DO ITEM,CRIEI REGRINHA DE VALIDAR UM BOOLEAN,
             *TAMBEM ALTERAR A IMAGEM COM UMA ANIMAÇÃO SIMPLES.
             */
            mBinding.buttonExpland.setOnClickListener {
                try {
                    if (mBinding.rvInnerVolumes.visibility == View.GONE) {
                        mBinding.buttonExpland.animate().apply {
                            duration = 600
                            rotationXBy(180f)
                        }.start()
                        mBinding.buttonExpland.setImageResource(R.drawable.ic_baseline_remove_24_cinza)
                        mBinding.rvInnerVolumes.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = InnerRvVol(listItem.listaNumeroSerie)
                            visibility = View.VISIBLE
                        }
                    } else {
                        mBinding.rvInnerVolumes.visibility = View.GONE
                        mBinding.buttonExpland.setImageResource(R.drawable.ic_baseline_add_24_cinza)
                        mBinding.buttonExpland.animate().apply {
                            duration = 600
                            rotationYBy(180f)
                        }.start()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Erros ao abrir lista!", Toast.LENGTH_SHORT).show()
                }
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
}


/** CLASSE DA RECYCLERVIEW INTERNA -->*/
class InnerRvVol(private val listaNumeroSerie: List<NumSerieVolModel>) :
    RecyclerView.Adapter<InnerRvVol.ViewHolderListAdapterInnerVol>() {
    private var mListInner = listaNumeroSerie


    inner class ViewHolderListAdapterInnerVol(private val binding: ItemRvVolumesInnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInner(numSerieVolModel: NumSerieVolModel) {
            with(binding) {
                apiNumSerieInner.text = numSerieVolModel.numeroSerie
            }
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderListAdapterInnerVol {
        val binding =
            ItemRvVolumesInnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderListAdapterInnerVol(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderListAdapterInnerVol, position: Int) {
        holder.bindInner(listaNumeroSerie[position])
    }

    override fun getItemCount() = mListInner.size

}
