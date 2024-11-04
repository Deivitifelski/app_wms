package com.documentos.wms_beirario.ui.consultaAuditoria.DialogFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.documentos.wms_beirario.databinding.DialogFragmentAuditoriaFinishBinding
import com.documentos.wms_beirario.model.auditoria.ResponseFinishAuditoriaItem
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3

class DialogFragmentFinishAuditoria(
    val mAuditoriaSelect: ResponseFinishAuditoriaItem,
) : DialogFragment() {

    private var mBinding: DialogFragmentAuditoriaFinishBinding? = null
    private val binding get() = mBinding

    interface Back {
        fun backClick(item: ResponseFinishAuditoriaItem, toInt: String)
    }

    private lateinit var mInterface: Back
    private lateinit var mAlertDialog: CustomAlertDialogCustom
    private lateinit var mSons: CustomMediaSonsMp3

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogFragmentAuditoriaFinishBinding.inflate(layoutInflater)
        initConst()
        setupDialogShow()
        initEdit()
        clickButtonQnt()
        setText(mAuditoriaSelect)
        mInterface = context as Back
        return binding!!.root
    }

    private fun initConst() {
        mBinding!!.editAuditoriaFinish.requestFocus()
        mSons = CustomMediaSonsMp3()
        mAlertDialog = CustomAlertDialogCustom()
        mBinding!!.editQnt.setText(mAuditoriaSelect.quantidade.toString())
        setupChangedEdit()
    }

    private fun setupChangedEdit() {
        /**EDITANDO MANUALMENTE A QUANTIDADE -->*/
        mBinding!!.editQnt.doAfterTextChanged { newTxt ->
            if (newTxt.isNullOrEmpty() || newTxt.toString() == "") {
                mBinding!!.editQnt.setText("0")
            } else {
                if (newTxt.first().toString() == "0" && newTxt.length > 1) {
                    val txtEdit = newTxt.removeRange(0, 1)
                    mBinding!!.editQnt.setText(txtEdit)
                }
                mBinding!!.editQnt.setSelection(mBinding!!.editQnt.length())
            }
        }
        /**BUTTON SIMPLES ADD ++ -->*/
        mBinding!!.buttonAddAuditoria.setOnClickListener {
            var text = mBinding!!.editQnt.text.toString().toInt()
            text += 1
            mBinding!!.editQnt.setText(text.toString())
        }
        /**BUTTON SIMPLES REMOVER -->*/
        mBinding!!.buttonRemoveAuditoria.setOnClickListener {
            var text = mBinding!!.editQnt.text.toString().toInt()
            text -= 1
            if (text <= 0) {
                mBinding!!.editQnt.setText("0")
                mBinding!!.editQnt.setSelection(mBinding!!.editQnt.length())
            } else {
                mBinding!!.editQnt.setSelection(mBinding!!.editQnt.length())
                mBinding!!.editQnt.setText(text.toString())
            }
        }
    }

    private fun clickButtonQnt() {
        mBinding!!.buttonAlterarQnt.setOnClickListener {
            if (mBinding!!.linearAlterarQnt.visibility == View.VISIBLE) {
                mBinding!!.linearAlterarQnt.visibility = View.GONE
            } else {
                mBinding!!.linearAlterarQnt.visibility = View.VISIBLE
            }
        }
        //BUtton LImpar -->
        mBinding!!.buttonLimpar.setOnClickListener {
            mBinding!!.editQnt.setText(mAuditoriaSelect.quantidade.toString())
        }
    }

    private fun initEdit() {
        mBinding!!.editAuditoriaFinish.setOnKeyListener { _, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == 10036 || keyCode == 103 || keyCode == 102) && event.action == KeyEvent.ACTION_UP) {
                Log.e(
                    "TAG", "CODIGO RECEBIDO ==== ${mBinding!!.editAuditoriaFinish.text.toString()}"
                )
                try {
                    if (mBinding!!.editAuditoriaFinish.text.toString().isNotEmpty()) {
                        if (mBinding!!.editAuditoriaFinish.text.toString() == mAuditoriaSelect.codBarrasEndereco) {
                            mInterface.backClick(item = mAuditoriaSelect,mBinding!!.editQnt.text.toString())
                            dismiss()
                        } else {
                            Log.e(
                                "ERROR",
                                "CÓDIGO BIPADO: ${mBinding!!.editAuditoriaFinish.text.toString()} || (NÃO) contem na lista"
                            )
                            mAlertDialog.alertMessageErrorSimples(
                                requireContext(),
                                "Endereço não encontrado ou não Contido na auditoria selecionada!",
                                4000
                            )
                            mBinding!!.editAuditoriaFinish.requestFocus()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                }
                clearEdit()
            }
            false
        }
    }

    private fun clearEdit() {
        mBinding!!.editAuditoriaFinish.apply {
            setText("")
            text!!.clear()
            requestFocus()
        }
    }

    private fun setText(item: ResponseFinishAuditoriaItem) {
        mBinding!!.endVisual.text = item.enderecoVisual
        mBinding!!.grade.text = item.codigoGrade
        mBinding!!.sku.text = item.sku
        mBinding!!.quantidade.text = item.quantidade.toString()
    }

    private fun setupDialogShow() {
        mBinding!!.toolbarAuditoriFinish.apply {
            setNavigationOnClickListener {
                dismiss()
            }
            title = mAuditoriaSelect.enderecoVisual
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}