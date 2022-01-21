package com.documentos.wms_beirario.ui.testes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.databinding.ItemSearchMockBinding
import java.util.*


class AdapterSearch(val click: (CityMock) -> Unit) :
    RecyclerView.Adapter<AdapterSearch.SearchViewHolder>() {

    private var mListSearckCity: MutableList<CityMock> = mutableListOf()

    inner class SearchViewHolder(val bindin: ItemSearchMockBinding) :
        RecyclerView.ViewHolder(bindin.root) {

        fun geraItem(city: CityMock) {
            with(bindin) {
                bindin.mockId.text = city.id.toString()
                bindin.mockCity.text = city.city
            }
            itemView.setOnClickListener {
                click.invoke(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemSearchMockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.geraItem(mListSearckCity[position])
    }

    override fun getItemCount() = mListSearckCity.size
    fun update(it: List<CityMock>) {
        mListSearckCity.clear()
        mListSearckCity.addAll(it)
        notifyDataSetChanged()
    }

    fun clear() {
        mListSearckCity.clear()
        notifyDataSetChanged()
    }
}



