package com.documentos.wms_beirario.ui.Tarefas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.FragmentTipoTarefaBinding

class TipoTarefaFragment : Fragment() {

    private var binding: FragmentTipoTarefaBinding? = null
    private val mBinding get() = binding!!
    private lateinit var mViewModel: TipoTarefaViewModel
    private lateinit var mShared: CustomSharedPreferences
    private var mRetrofitService = RetrofitService.getInstance()
    private lateinit var mToken: String
    private var mIdArmazem: Int = 0
    private var mAdapter: AdapterTipoTarefa = AdapterTipoTarefa {
        findNavController().navigate(R.id.armazenagemFragment)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipoTarefaBinding.inflate(inflater, container, false)


        mShared = CustomSharedPreferences(requireContext())

        mViewModel = ViewModelProvider(
            this,
            TipoTarefaViewModelFactory(TipoTarefaRepository(mRetrofitService))
        ).get(TipoTarefaViewModel::class.java)

        initShared()
        initData()
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        initViewModel()
    }


    private fun initShared() {
        mToken = mShared.getString(CustomSharedPreferences.TOKEN)!!
        mIdArmazem = mShared.getInt(CustomSharedPreferences.ID_ARMAZEM)!!
    }

    private fun initData() {
        mViewModel.getTarefas(mToken, mIdArmazem)
    }

    private fun initViewModel() {
        mViewModel.mResponseSucess.observe(this, Observer { listTarefas ->
            mBinding.apply {
                //RECYCLERVIEW-->
                rvTipoTarefa.apply {
                    this.layoutManager = GridLayoutManager(requireContext(), 2)
                    this.adapter = mAdapter
                }
            }
            mAdapter.update(listTarefas)
        })

        mViewModel.mResponseError.observe(this, Observer { erro ->
            Toast.makeText(requireContext(), erro.toString(), Toast.LENGTH_SHORT).show()
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}