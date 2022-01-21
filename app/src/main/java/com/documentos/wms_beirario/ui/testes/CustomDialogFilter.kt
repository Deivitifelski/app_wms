package com.documentos.wms_beirario.ui.testes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.CustomAlertSeachBinding
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionFragment
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class CustomDialogFilter(val list: MutableList<CityMock>) : DialogFragment() {

    private var mBinding: CustomAlertSeachBinding? = null
    val binding get() = mBinding!!
    private lateinit var mAdapterSearch: AdapterSearch
    private lateinit var mCityInit: CityMock
    private lateinit var mListCityInit: MutableList<CityMock>
    private lateinit var mPalavraCorrent : String

    interface callResult {
        fun result(city: CityMock)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.ThemeOverlay_Material_Dialog_Alert)
        mListCityInit = list
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = CustomAlertSeachBinding.inflate(layoutInflater)
        mBinding!!.buttonSelectSearch.isEnabled = false
        setupDialogShow()
        return binding.root
    }

    private fun setupDialogShow() {
        /**FILTRANDO PELO EDIT ->*/
        mBinding!!.editTextSearch.doAfterTextChanged { caracter ->
                    filterList(caracter.toString())
            if (caracter!!.isEmpty()){
                mBinding!!.buttonSelectSearch.isEnabled = false
               hideKeyExtensionFragment(mBinding!!.editTextSearch)
            }
        }

        /**INICIANDO ADAPTER ->*/
        mAdapterSearch = AdapterSearch { itemClick ->
            mPalavraCorrent = itemClick.city
            mBinding!!.editTextSearch.setText(itemClick.city)
            mAdapterSearch.clear()
            mCityInit = itemClick
            mBinding!!.buttonSelectSearch.isEnabled = true
        }
        /**setup recyclerview -->*/
        mBinding!!.rvSearch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapterSearch
        }
        mAdapterSearch.update(mListCityInit)
        /**BUTTON FECHAR -->*/
        mBinding!!.buttonCloseSearch.setOnClickListener {
            dismiss()
        }
        /**BUTTON SELECT -->*/
        mBinding!!.buttonSelectSearch.setOnClickListener {
            (activity as (callResult)).result(mCityInit)
            dismiss()
        }
    }

    private fun filterList(caracter: String) {
        val newList = mutableListOf<CityMock>()
        for (item in mListCityInit) {
            if (item.city.lowercase().contains(caracter.lowercase()) ||
                item.id.toString().lowercase().contains(caracter.lowercase())
            ) {
                newList.add(item)
            }
        }
        mAdapterSearch.update(newList)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}