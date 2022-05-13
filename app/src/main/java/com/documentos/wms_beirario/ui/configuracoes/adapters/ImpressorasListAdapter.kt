import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemPrinterBinding

class ImpressorasListAdapter(private var mOnClick: (BluetoothDevice) -> Unit) :
    RecyclerView.Adapter<ImpressorasListAdapter.ImpressorasViewHolder>() {

    private var mListDevices = mutableListOf<BluetoothDevice>()

    inner class ImpressorasViewHolder(val mBinding: ItemPrinterBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(bluetoothDevice: BluetoothDevice) {
            with(bluetoothDevice) {
                if (mBinding.tvImpressoraName.text.isNullOrEmpty()) {
                    mBinding.tvImpressoraName.text = "Nome indisponivel"
                }
                mBinding.tvImpressoraName.text = this.name
                mBinding.tvImpressoraAddress.text = this.address
            }
            itemView.setOnClickListener {
                mOnClick.invoke(bluetoothDevice)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImpressorasViewHolder {
        val binding =
            ItemPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImpressorasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImpressorasViewHolder, position: Int) {
        if (mListDevices.size > 0) {
            holder.bind(mListDevices[position])
        }
    }

    override fun getItemCount() = mListDevices.size


    fun update(mListBluetooh: List<BluetoothDevice>) {
        if (mListDevices.containsAll(mListBluetooh)) {
            mListDevices.removeAll(mListBluetooh)
        } else {
            mListDevices.addAll(mListBluetooh)
        }
        notifyDataSetChanged()
    }

    fun clear() {
        mListDevices.clear()
        notifyDataSetChanged()
    }
}