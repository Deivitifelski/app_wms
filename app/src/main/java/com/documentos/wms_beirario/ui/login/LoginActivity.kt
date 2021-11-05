package com.documentos.wms_beirario.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitService
import com.documentos.wms_beirario.databinding.ActivityMainBinding
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity


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
    }

    private fun initResponse() {
        mLoginViewModel.mLoginSucess.observe(this, Observer { token ->
            startActivity(token)
        })
        mLoginViewModel.mLoginErrorUser.observe(this, Observer {

            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        })
        mLoginViewModel.mLoginErrorServ.observe(this, Observer {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        })
        mLoginViewModel.mValidaLogin.observe(this, Observer {
            if (it == true) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
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
            val usuario = mBinding.editUsuarioLogin.text.toString()
            val senha = mBinding.editSenhaLogin.text.toString()
            mLoginViewModel.getToken(usuario, senha)

        }
    }
}