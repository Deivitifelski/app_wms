package com.documentos.wms_beirario.ui.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityMainBinding
import com.documentos.wms_beirario.databinding.LayoutTrocarUserBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.repository.LoginRepository
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var mRetrofitService = RetrofitService.getInstance()
    private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mDialog = CustomAlertDialogCustom().progress(this,getString(R.string.checking_user))
        mLoginViewModel = ViewModelProvider(
            this,
            LoginViewModel.LoginViewModelFactory(LoginRepository(mRetrofitService))
        ).get(LoginViewModel::class.java)
        mSharedPreferences = CustomSharedPreferences(this)
        initResponse()
    }

    override fun onResume() {
        super.onResume()
        mDialog.hide()
        initUser()
        alertLogin()


    }


    private fun initResponse() {

        mLoginViewModel.mLoginSucess.observe(this, { token ->
            mDialog.hide()
            CustomMediaSonsMp3().somSucess(this)
            startActivity(token)
        })
        mLoginViewModel.mLoginErrorUser.observe(this, { message ->
            mDialog.hide()
            CustomMediaSonsMp3().somError(this)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel.mLoginErrorServ.observe(this, { message ->
            mDialog.hide()
            CustomMediaSonsMp3().somError(this)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel.mValidaLogin.observe(this, {
            mDialog.hide()
            CustomMediaSonsMp3().somError(this)
            if (it == true) {
                CustomSnackBarCustom().snackBarErrorSimples(
                    mBinding.layoutLoginTest,
                    "Preencha todos os Campos!"
                )
            }
        })
    }

    private fun startActivity(token: String) {
        RetrofitService.TOKEN = token
        startActivity(Intent(this, ArmazensActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun initUser() {
        mBinding.buttonLogin.setOnClickListener {
            mDialog.show()
            val usuario = mBinding.editUsuarioLogin.text.toString()
            val senha = mBinding.editSenhaLogin.text.toString()
            mSharedPreferences.saveString(CustomSharedPreferences.NAME_USER, usuario)
            mSharedPreferences.saveString(CustomSharedPreferences.SENHA_USER, senha)
            mLoginViewModel.getToken(usuario, senha)

        }
    }

    private fun alertLogin() {
        CustomMediaSonsMp3().somAlerta(this)
        val mAlert = android.app.AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val mBindingdialog = LayoutTrocarUserBinding.inflate(LayoutInflater.from(this))
        mAlert.apply {
            setView(mBindingdialog.root)
        }
        val mShow = mAlert.show()
        mBindingdialog.buttonSim.setOnClickListener {
            mShow.dismiss()
        }
        mBindingdialog.buttonNao.setOnClickListener {
            val usuario = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER)
            val senha = mSharedPreferences.getString(CustomSharedPreferences.SENHA_USER)
            if (usuario.isNullOrEmpty() || senha.isNullOrEmpty()){
                CustomSnackBarCustom().snackBarPadraoSimplesBlack(mBinding.layoutLoginTest,"Ops...Faça o login novamente!")
            }else{
                mShow.dismiss()
                mDialog.show()
                Handler().postDelayed({
                    mLoginViewModel.getToken(usuario, senha)
                },1000)
            }
        }
        mAlert.create()
    }
}