package com.documentos.wms_beirario.ui.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.documentos.wms_beirario.utils.extensions.shake
import com.documentos.wms_beirario.utils.extensions.vibrateExtension
import com.example.coletorwms.constants.CustomMediaSonsMp3
import com.example.coletorwms.constants.CustomSnackBarCustom


class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var mRetrofitService = ServiceApi.getInstance()
    private lateinit var mLoginViewModel: LoginViewModel
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mDialog: Dialog
    private val responseLaucher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val txt = result.data?.getStringExtra("rota_alterada")!!
                mBinding.tolbarLogin.subtitle = txt
            } else {
                mBinding.tolbarLogin.subtitle = "Desenvolvimento"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.tolbarLogin)
        mDialog = CustomAlertDialogCustom().progress(this, getString(R.string.checking_user))
        mLoginViewModel = ViewModelProvider(
            this,
            LoginViewModel.LoginViewModelFactory(LoginRepository(mRetrofitService))
        )[LoginViewModel::class.java]
        mSharedPreferences = CustomSharedPreferences(this)
        setupObservables()
        initUser()
    }

    override fun onResume() {
        super.onResume()
        mDialog.hide()
        alertLogin()
    }


    private fun setupObservables() {
        mLoginViewModel.mLoginSucess.observe(this, { token ->
            mSharedPreferences.saveString(CustomSharedPreferences.TOKEN,token.toString())
            mDialog.hide()
            CustomMediaSonsMp3().somSucess(this)
            startActivity(token)
        })
        mLoginViewModel.mLoginErrorUser.observe(this, { message ->
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
        ServiceApi.TOKEN = token
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
            initUser()
            val usuario = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER)
            val senha = mSharedPreferences.getString(CustomSharedPreferences.SENHA_USER)
            if (usuario.isNullOrEmpty() || senha.isNullOrEmpty()) {
                CustomSnackBarCustom().snackBarPadraoSimplesBlack(
                    mBinding.layoutLoginTest,
                    "Ops...Faça o login novamente!"
                )
            } else {
                mShow.dismiss()
                mDialog.show()
                Handler().postDelayed({
                    mLoginViewModel.getToken(usuario, senha)
                }, 1000)
            }
        }
        mAlert.create()
    }

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
        binding.editSenhaFiltrar.showSoftInputOnFocus = false
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding.editUsuarioFiltrar.requestFocus()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        binding.buttonValidad.setOnClickListener {
            if (binding.editUsuarioFiltrar.text.toString() == mSenhaUserAcesso && binding.editSenhaFiltrar.text.toString() == mSenhaAcesso) {
                CustomMediaSonsMp3().somClick(this)
                val intent = Intent(this, AlterarRotaActivity::class.java)
                responseLaucher.launch(intent)
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

    }
}