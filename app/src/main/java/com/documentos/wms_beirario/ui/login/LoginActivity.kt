package com.documentos.wms_beirario.ui.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityMainBinding
import com.documentos.wms_beirario.databinding.LayoutAlertdialogCustomPortaBinding
import com.documentos.wms_beirario.databinding.LayoutTrocarUserBinding
import com.documentos.wms_beirario.repository.login.LoginRepository
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.extensions.hideKeyExtensionActivity
import com.documentos.wms_beirario.utils.extensions.shake
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var mLoginViewModel: LoginViewModel? = null
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.tolbarLogin)
        mDialog = CustomAlertDialogCustom().progress(this, getString(R.string.checking_user))
        mSharedPreferences = CustomSharedPreferences(this)
        initUser()
    }

    override fun onResume() {
        super.onResume()
        setTxtRota()
        mDialog.hide()
        alertLogin()
        validButton()
        mBinding.buttonLogin.isEnabled = false
        setButtons()
    }

    private fun setTxtRota() {
        if (ServiceApi.mRotaApi) {
            mBinding.tolbarLogin.subtitle = getString(R.string.base_product)
        } else {
            mBinding.tolbarLogin.subtitle = getString(R.string.base_development)
        }
    }

    private fun setButtons() {
        mBinding.editSenhaLogin.addTextChangedListener {
            validButton()
        }
        mBinding.editUsuarioLogin.addTextChangedListener {
            validButton()
        }
    }

    private fun validButton() {
        mBinding.editSenhaLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mBinding.buttonLogin.isEnabled =
                    mBinding.editUsuarioLogin.text!!.isNotEmpty() && s.toString().isNotEmpty()
            }
        })
        mBinding.editUsuarioLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mBinding.buttonLogin.isEnabled =
                    mBinding.editSenhaLogin.text!!.isNotEmpty() && s.toString().isNotEmpty()
            }
        })
    }


    private fun setupObservables() {
        mLoginViewModel!!.mLoginSucess.observe(this, { token ->
            mSharedPreferences.saveString(CustomSharedPreferences.TOKEN, token.toString())
            mDialog.hide()
            CustomMediaSonsMp3().somSucess(this)
            startActivity(token)
        })
        mLoginViewModel!!.mLoginErrorUser.observe(this, { message ->
            mDialog.hide()
            CustomMediaSonsMp3().somError(this)
            if (message == "USUARIO INVALIDO!") {
                mBinding.usuario.requestFocus()
                mBinding.usuario.shake {
                    CustomSnackBarCustom().snackBarErrorSimples(
                        mBinding.layoutLoginTest,
                        message.toString()
                    )
                }
            } else {
                mBinding.senha.requestFocus()
                mBinding.senha.shake {
                    CustomSnackBarCustom().snackBarErrorSimples(
                        mBinding.layoutLoginTest,
                        message.toString()
                    )
                }
            }
        })
        mLoginViewModel!!.mLoginErrorServ.observe(this, { message ->
            mDialog.hide()
            CustomMediaSonsMp3().somError(this)
            CustomSnackBarCustom().snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        })
        mLoginViewModel!!.mValidaLogin.observe(this, {
            mDialog.hide()
            CustomMediaSonsMp3().somError(this)
            if (it == true) {
                CustomSnackBarCustom().snackBarErrorSimples(
                    mBinding.layoutLoginTest,
                    "Preencha todos os Campos!"
                )
            }
        })

        mLoginViewModel!!.mValidaButton.observe(this) { validButton ->
            mBinding.buttonLogin.isEnabled = validButton
        }
    }

    private fun startActivity(token: String) {
        ServiceApi.TOKEN = token
        startActivity(Intent(this, ArmazensActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun initUser() {
        mBinding.buttonLogin.setOnClickListener {
            setupCallService()
            mDialog.show()
            val usuario = mBinding.editUsuarioLogin.text.toString()
            val senha = mBinding.editSenhaLogin.text.toString()
            mSharedPreferences.saveString(CustomSharedPreferences.NAME_USER, usuario)
            mSharedPreferences.saveString(CustomSharedPreferences.SENHA_USER, senha)
            mLoginViewModel!!.getToken(usuario, senha)
        }
    }

    private fun setupCallService() {
        val mRetrofitService = ServiceApi.getInstance()
        mLoginViewModel = null
        mLoginViewModel = ViewModelProvider(
            this,
            LoginViewModel.LoginViewModelFactory(LoginRepository(mRetrofitService))
        )[LoginViewModel::class.java]
        setupObservables()
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
            initUser()
            val usuario = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER)
            val senha = mSharedPreferences.getString(CustomSharedPreferences.SENHA_USER)
            if (usuario.isNullOrEmpty() || senha.isNullOrEmpty()) {
                CustomSnackBarCustom().snackBarPadraoSimplesBlack(
                    mBinding.layoutLoginTest,
                    "Ops...Faça o login novamente!"
                )
            } else {
                setupCallService()
                mShow.dismiss()
                mDialog.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    mLoginViewModel!!.getToken(usuario, senha)
                }, 1000)
            }
        }
        mAlert.create()
    }

    /**CLICK MENU ALTERAR ROTA----------->*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_change_route, menu)
        return true
    }

    //Click Menu -->
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_rota -> {
                alterarPorta()
            }
        }
        return true
    }

    //ALERTDIALOG TROCA ROTA 5001/5002 -->
    private fun alterarPorta() {
        val mSenhaUserAcesso = "a"
        val mSenhaAcesso = "a"
        CustomMediaSonsMp3().somClick(this)
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(this)
        val binding = LayoutAlertdialogCustomPortaBinding.inflate(layoutInflater)
        mAlert.setCancelable(false)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        //Escondendo teclado -->
        hideKeyExtensionActivity(binding.editSenhaFiltrar)
        binding.editUsuarioFiltrar.requestFocus()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        binding.buttonValidad.setOnClickListener {
            if (binding.editUsuarioFiltrar.text.toString() == mSenhaUserAcesso && binding.editSenhaFiltrar.text.toString() == mSenhaAcesso) {
                CustomMediaSonsMp3().somClick(this)
                val intent = Intent(this, AlterarRotaActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                mShow.dismiss()
            } else {
                vibrateExtension(500)
                CustomAlertDialogCustom().alertMessageErrorCancelFalse(
                    this,
                    "usuário ou senha inválidos"
                )
            }
        }
        binding.buttonClose.setOnClickListener {
            mShow.dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}