package com.documentos.wms_beirario.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityMainBinding
import com.documentos.wms_beirario.databinding.LayoutTrocarUserBinding
import com.documentos.wms_beirario.extensions.AppExtensions
import com.documentos.wms_beirario.repository.LoginRepository
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var mRetrofitService = RetrofitService.getInstance()
    private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mLoginViewModel = ViewModelProvider(
            this,
            LoginViewModel.LoginViewModelFactory(LoginRepository(mRetrofitService))
        ).get(LoginViewModel::class.java)
        mSharedPreferences = CustomSharedPreferences(this)
        initResponse()
    }

    override fun onResume() {
        super.onResume()
        initUser()
        AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
        alertLogin()


    }


    private fun initResponse() {

        mLoginViewModel.mLoginSucess.observe(this, { token ->
            CustomMediaSonsMp3().somSucess(this)
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
            startActivity(token)
        })
        mLoginViewModel.mLoginErrorUser.observe(this, { message ->
            CustomMediaSonsMp3().somError(this)
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel.mLoginErrorServ.observe(this, { message ->
            CustomMediaSonsMp3().somError(this)
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel.mValidaLogin.observe(this, {
            CustomMediaSonsMp3().somError(this)
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = false)
            if (it == true) {
                CustomSnackBarCustom().snackBarErrorSimples(
                    mBinding.layoutLoginTest,
                    "Preencha todos os Campos!"
                )
            }
        })
    }

    private fun startActivity(token: String) {
        mSharedPreferences.saveString(CustomSharedPreferences.TOKEN, token)
        startActivity(Intent(this, ArmazensActivity::class.java))
    }

    private fun initUser() {
        mBinding.buttonLogin.setOnClickListener {
            AppExtensions.visibilityProgressBar(mBinding.progress, visibility = true)
            val usuario = mBinding.editUsuarioLogin.text.toString()
            val senha = mBinding.editSenhaLogin.text.toString()
            mSharedPreferences.saveString(CustomSharedPreferences.NAME_USER, usuario)
            mSharedPreferences.saveString(CustomSharedPreferences.SENHA_USER, senha)
            mLoginViewModel.getToken(usuario, senha)

        }
    }

    private fun alertLogin() {
        CustomMediaSonsMp3().somSucess(this)
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
                AppExtensions.visibilityProgressBar(mBinding.progress,true)
                Handler().postDelayed({
                    mLoginViewModel.getToken(usuario, senha)
                    AppExtensions.visibilityProgressBar(mBinding.progress,false)
                },1000)
            }
        }
        mAlert.create()
    }
}