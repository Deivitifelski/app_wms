import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemRvLabeling3Binding
import com.documentos.wms_beirario.model.etiquetagem.EtiquetagemResponse3

class AdapterLabeling3() :
    ListAdapter<EtiquetagemResponse3, AdapterLabeling3.AdapterPendingViewHolder3>(DiffUtilPending3()) {

    inner class AdapterPendingViewHolder3(val mBinding: ItemRvLabeling3Binding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: EtiquetagemResponse3) {
            mBinding.itPedido.text = item.numeroPedido.toString()
            mBinding.itPendentes.text = item.quantidadePendente.toString()
            mBinding.itVolume.text = item.quantidadeVolumes.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPendingViewHolder3 {
        val binding =
            ItemRvLabeling3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterPendingViewHolder3(binding)
    }

    override fun onBindViewHolder(holder: AdapterPendingViewHolder3, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilPending3() : DiffUtil.ItemCallback<EtiquetagemResponse3>() {
    override fun areItemsTheSame(
        oldItem: EtiquetagemResponse3,
        newItem: EtiquetagemResponse3
    ): Boolean {
        return oldItem.numeroPedido == newItem.numeroPedido
    }

    override fun areContentsTheSame(
        oldItem: EtiquetagemResponse3,
        newItem: EtiquetagemResponse3
    ): Boolean {
        return oldItem == newItem
    }

}
