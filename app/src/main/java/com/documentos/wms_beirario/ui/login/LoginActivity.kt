package com.documentos.wms_beirario.ui.login

import ChangedBaseUrlDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.data.CustomSharedPreferences
import com.documentos.wms_beirario.data.RetrofitClient
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.databinding.ActivityLoginBinding
import com.documentos.wms_beirario.databinding.LayoutAlertdialogCustomPortaBinding
import com.documentos.wms_beirario.databinding.LayoutTrocarUserBinding
import com.documentos.wms_beirario.repository.login.LoginRepository
import com.documentos.wms_beirario.ui.armazens.ArmazensActivity
import com.documentos.wms_beirario.utils.CustomAlertDialogCustom
import com.documentos.wms_beirario.utils.CustomMediaSonsMp3
import com.documentos.wms_beirario.utils.CustomSnackBarCustom
import com.documentos.wms_beirario.utils.extensions.*

class LoginActivity : AppCompatActivity(), ChangedBaseUrlDialog.sendBase {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mSharedPreferences: CustomSharedPreferences
    private lateinit var mDialog: Dialog
    private lateinit var mSnackBarCustom: CustomSnackBarCustom
    private var mViewModel: LoginViewModel? = null
    private var click: Boolean = false
    private val mResponseBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                clearEdits()
                initViewModel()
                alertLogin()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.tolbarLogin)
        mSharedPreferences = CustomSharedPreferences(this)
        initConst()
        validButton()
        click()
        initViewModel()
        clearEdits()
        /**
         * REMOVER PARA ENTREGAR UMA VERSÃO -->
         */
        alertLogin()
    }

    /**INICIA AS CONTANTES || DEVE INICIAR SEMPRE EM PRODUÇÃO -->*/
    private fun initConst() {
        val tipoBanco = mSharedPreferences.getString("TIPO_BANCO")
        if (tipoBanco.isNullOrEmpty()) {
            val base = "http://10.0.1.111:5001/wms/"
            val title = getString(R.string.produce)
            mSharedPreferences.saveString("TIPO_BANCO", title)
            mSharedPreferences.saveString("BASE_URL", base)
            RetrofitClient.baseUrl = base
            val banco = mSharedPreferences.getString("TIPO_BANCO").toString()
            mBinding.tolbarLogin.subtitle = "$banco [${getVersion()}]"
        } else {
            RetrofitClient.baseUrl = mSharedPreferences.getString("BASE_URL").toString()
            mBinding.tolbarLogin.subtitle = "$tipoBanco [${getVersion()}]"
        }
        mSnackBarCustom = CustomSnackBarCustom()
        mDialog = CustomAlertDialogCustom().progress(this, "Verificando seu login...")
        mDialog.hide()
    }

    private fun initViewModel() {
        Log.e("LOGIN", "initViewModel BASE URL = ${RetrofitClient.baseUrl} ")
        mViewModel = ViewModelProvider(
            this,
            LoginViewModel.LoginViewModelFactory(LoginRepository())
        )[LoginViewModel::class.java]
        setObervable()
    }

    /**RESULTADOS DO VIEWMODEL -->*/
    private fun setObervable() {
        mViewModel!!.mLoginSucess.observe(this) { token ->
            mSharedPreferences.saveString(CustomSharedPreferences.TOKEN, token.toString())
            startActivity(token)
        }
        mViewModel!!.mLoginErrorUser.observe(this) { message ->
            CustomMediaSonsMp3().somError(this)
            if (message == "USUARIO INVALIDO!") {
                mBinding.usuario.requestFocus()
                mBinding.usuario.shake {
                    mSnackBarCustom.snackBarErrorSimples(
                        mBinding.layoutLoginTest,
                        message.toString()
                    )
                }
            } else {
                vibrateExtension()
                mBinding.senha.requestFocus()
                mBinding.senha.shake {
                    mSnackBarCustom.snackBarErrorSimples(
                        mBinding.layoutLoginTest,
                        message.toString()
                    )
                }
            }
        }
        mViewModel!!.mLoginErrorServ.observe(this) { message ->
            CustomMediaSonsMp3().somError(this)
            mSnackBarCustom.snackBarErrorSimples(
                mBinding.layoutLoginTest,
                message.toString()
            )
        }

        mViewModel!!.mErrorAllShow.observe(this) { errorAll ->
            CustomMediaSonsMp3().somError(this)
            mSnackBarCustom.snackBarErrorSimples(
                mBinding.layoutLoginTest,
                errorAll.toString()
            )
        }
        mViewModel!!.mProgressShow.observe(this) { progress ->
            if (progress) {
                mDialog.show()
            } else {
                mDialog.hide()
            }
        }
    }

    /**click button entrar -->*/
    private fun click() {
        mBinding.buttonLogin.setOnClickListener {
            saveUserShared()
            mViewModel!!.getToken(
                mBinding.editUsuarioLogin.text.toString().trim(),
                mBinding.editSenhaLogin.text.toString().trim()
            )
        }
    }

    private fun saveUserShared() {
        val usuario = mBinding.editUsuarioLogin.text.toString().trim()
        val senha = mBinding.editSenhaLogin.text.toString().trim()
        mSharedPreferences.saveString(CustomSharedPreferences.NAME_USER, usuario)
        mSharedPreferences.saveString(CustomSharedPreferences.SENHA_USER, senha)
    }

    /**INICIA PROXIMA ACTIVITY -->*/
    private fun startActivity(token: String) {
        ServiceApi.TOKEN = token
        val intent = Intent(this, ArmazensActivity::class.java)
        mResponseBack.launch(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**QUANDO CAMPO USER E SENHA NAO FOR VAZIO HABILITA O BUTTON DE LOGIN -->*/
    private fun validButton() {
        mBinding.editSenhaLogin.changedEditText { editSenha(mBinding.editSenhaLogin.text.toString()) }
        mBinding.editUsuarioLogin.changedEditText { editUser(mBinding.editUsuarioLogin.text.toString()) }
    }

    private fun editUser(s: String) {
        mBinding.buttonLogin.isEnabled =
            mBinding.editSenhaLogin.text!!.isNotEmpty() && s.isNotEmpty()
    }

    private fun editSenha(s: String) {
        mBinding.buttonLogin.isEnabled =
            mBinding.editUsuarioLogin.text!!.isNotEmpty() && s.isNotEmpty()
    }

    /**DIALOG ONDE O USUARIO PODE SELECIONAR SE DESEJA ALTERAR OU CONTINUAR COM USUARIO -->*/
    private fun alertLogin() {
        vibrateExtension(500)
        CustomMediaSonsMp3().somAlerta(this)
        val mAlert = android.app.AlertDialog.Builder(this)
        mAlert.setCancelable(false)
        val mBindingdialog = LayoutTrocarUserBinding.inflate(LayoutInflater.from(this))
        mAlert.apply {
            setView(mBindingdialog.root)
        }
        val mShow = mAlert.show()
        mBindingdialog.buttonSim.setOnClickListener {
            mBinding.editUsuarioLogin.setText("")
            mBinding.editSenhaLogin.setText("")
            mShow.hide()
        }
        mBindingdialog.buttonNao.setOnClickListener {
            mShow.hide()
            val usuario = mSharedPreferences.getString(CustomSharedPreferences.NAME_USER)
            val senha = mSharedPreferences.getString(CustomSharedPreferences.SENHA_USER)
            if (usuario.isNullOrEmpty() || senha.isNullOrEmpty()) {
                mSnackBarCustom.snackBarPadraoSimplesBlack(
                    mBinding.layoutLoginTest,
                    "Ops...Faça o login novamente!"
                )
            } else {
                mShow.hide()
                mViewModel!!.getToken(usuario, senha)
                mDialog.show()
            }
        }
        mAlert.create()
    }

    /**CLICK MENU ALTERAR ROTA----------->*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
        return false
    }

    /**ALERTDIALOG TROCA ROTA 5001/5002 -->*/
    private fun alterarPorta() {
        val mSenhaUserAcesso = "paipe123"
        val mSenhaAcesso = "paipe123"
        CustomMediaSonsMp3().somClick(this)
        val mAlert = androidx.appcompat.app.AlertDialog.Builder(this)
        val binding = LayoutAlertdialogCustomPortaBinding.inflate(layoutInflater)
        mAlert.setCancelable(false)
        mAlert.setView(binding.root)
        val mShow = mAlert.show()
        binding.editUsuarioFiltrar.requestFocus()
        //Recebendo a leitura Coletor Finalizar Tarefa -->
        binding.buttonValidad.setOnClickListener {
            if (binding.editUsuarioFiltrar.text.toString()
                    .trim() == mSenhaUserAcesso && binding.editSenhaFiltrar.text.toString()
                    .trim() == mSenhaAcesso
            ) {
                CustomMediaSonsMp3().somClick(this)
                ChangedBaseUrlDialog().show(supportFragmentManager, "BASE_URL")
                mViewModel = null
                mShow.hide()
            } else {
                vibrateExtension(500)
                CustomAlertDialogCustom().alertMessageErrorCancelFalse(
                    this,
                    "usuário ou senha inválidos"
                )
            }
        }
        binding.buttonClose.setOnClickListener {
            mShow.hide()
        }
    }

    /**LIMPA OS EDITS -->*/
    private fun clearEdits() {
        mBinding.editSenhaLogin.text!!.clear()
        mBinding.editUsuarioLogin.text!!.clear()
        mBinding.editUsuarioLogin.requestFocus()
        showKeyExtensionActivity(mBinding.editUsuarioLogin)
    }

    /**RETORNO DA BASEURL SELECIONADA NO DIALOG -->*/
    override fun sendBaseDialog(base: String, title: String) {
        clearEdits()
        mSharedPreferences.saveString("TIPO_BANCO", title)
        mSharedPreferences.saveString("BASE_URL", base)
        RetrofitClient.baseUrl = base
        mBinding.tolbarLogin.subtitle = "$title [${getVersion()}]"
        initViewModel()
    }

    /**FUNÇÃO ONTEM SETA OS 2 CLIQUE PARA SAIR DO APP --> */
    override fun onBackPressed() {
        if (click) {
            finishAffinity()
        } else {
            click = true
            Handler(Looper.getMainLooper()).postDelayed({ click = false }, 2000)
            mSnackBarCustom.snackBarPadraoSimplesBlack(
                mBinding.root,
                "Clique novamente para fechar o aplicativo!"
            )
        }
    }
}