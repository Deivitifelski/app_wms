package com.documentos.wms_beirario.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityMainBinding
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
        AppExtensions.visibilityProgressBar(mBinding.progress,visibility = false)


    }

    private fun initResponse() {

        mLoginViewModel.mLoginSucess.observe(this, { token ->
            CustomMediaSonsMp3().somSucess(this)
            AppExtensions.visibilityProgressBar(mBinding.progress,visibility = false)
            startActivity(token)
        })
        mLoginViewModel.mLoginErrorUser.observe(this, { message ->
            CustomMediaSonsMp3().somError(this)
            AppExtensions.visibilityProgressBar(mBinding.progress,visibility = false)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel.mLoginErrorServ.observe(this, { message ->
            CustomMediaSonsMp3().somError(this)
            AppExtensions.visibilityProgressBar(mBinding.progress,visibility = false)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel.mValidaLogin.observe(this, {
            CustomMediaSonsMp3().somError(this)
            AppExtensions.visibilityProgressBar(mBinding.progress,visibility = false)
            if (it == true) {
                CustomSnackBarCustom().snackBarErrorSimples(mBinding.layoutLoginTest, "Preencha todos os Campos!")
            }
        })
    }

    private fun startActivity(token: String) {
        mSharedPreferences.saveString(CustomSharedPreferences.TOKEN, token)
        val intent = Intent(this, ArmazensActivity::class.java)
        startActivity(intent)

    }

    private fun initUser() {
        mBinding.buttonLogin.setOnClickListener {
            AppExtensions.visibilityProgressBar(mBinding.progress,visibility = true)
            val usuario = mBinding.editUsuarioLogin.text.toString()
            val senha = mBinding.editSenhaLogin.text.toString()
            mLoginViewModel.getToken(usuario, senha)

        }
    }
}