import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvLabelingReqBinding
import com.documentos.wms_beirario.model.etiquetagem.ResponseEtiquetagemRequisicaoItem
import com.documentos.wms_beirario.utils.extensions.AppExtensions

class AdapterLabelingRequisicao() :
    ListAdapter<ResponseEtiquetagemRequisicaoItem, AdapterLabelingRequisicao.AdapterPendingViewHolderRequisicao>(
        DiffUtilPendingRequisicao()
    ) {

    inner class AdapterPendingViewHolderRequisicao(val mBinding: ItemRvLabelingReqBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: ResponseEtiquetagemRequisicaoItem) {
            mBinding.apiRequisicao.text = item.requisicao.toString()
            mBinding.dataApi.text = AppExtensions.formatData(item.dataEmissao)
            mBinding.quantidadeVolumes.text = item.quantidadeVolumesPendentes.toString()
            mBinding.quantidadePendente.text = item.quantidadeVolumes.toString()
            mBinding.filialApi.text = "Filial ${item.filial}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterPendingViewHolderRequisicao {
        val binding =
            ItemRvLabelingReqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPendingViewHolderRequisicao(binding)
    }

    override fun onBindViewHolder(holder: AdapterPendingViewHolderRequisicao, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilPendingRequisicao() : DiffUtil.ItemCallback<ResponseEtiquetagemRequisicaoItem>() {
    override fun areItemsTheSame(
        oldItem: ResponseEtiquetagemRequisicaoItem,
        newItem: ResponseEtiquetagemRequisicaoItem
    ): Boolean {
        return oldItem.requisicao == newItem.requisicao
    }

    override fun areContentsTheSame(
        oldItem: ResponseEtiquetagemRequisicaoItem,
        newItem: ResponseEtiquetagemRequisicaoItem
    ): Boolean {
        return oldItem == newItem
    }

}
