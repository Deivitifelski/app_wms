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

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: CustomSharedPreferences
    private lateinit var mDialog: Dialog
    private lateinit var mSnackBarCustom: CustomSnackBarCustom
    private lateinit var alertDailog: CustomAlertDialogCustom
    private var viewModel: LoginViewModel? = null
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
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tolbarLogin)
        sharedPreferences = CustomSharedPreferences(this)
        initConst()
        validButton()
        click()
        initViewModel()
        clearEdits()
        /**
         * REMOVER PARA ENTREGAR UMA VERSÃO -->
         */
//        alertLogin()
    }

    override fun onResume() {
        super.onResume()
//        mBinding.editUsuarioLogin.setText("maria_rosa")
//        mBinding.editSenhaLogin.setText("beirario")
    }

    /**INICIA AS CONTANTES || DEVE INICIAR SEMPRE EM PRODUÇÃO -->*/
    private fun initConst() {

        alertDailog = CustomAlertDialogCustom()
        val tipoBanco = sharedPreferences.getString("TIPO_BANCO")
        if (tipoBanco.isNullOrEmpty()) {
            val base = "https://api-prd-internal.calcadosbeirario.com.br/coletor/wms/"
            val title = getString(R.string.produce)
            sharedPreferences.saveString("TIPO_BANCO", title)
            sharedPreferences.saveString("BASE_URL", base)
            RetrofitClient.baseUrl = base
            val banco = sharedPreferences.getString("TIPO_BANCO").toString()
            binding.tolbarLogin.subtitle = "$banco [${getVersion()}]"
        } else {
            RetrofitClient.baseUrl = sharedPreferences.getString("BASE_URL").toString()
            binding.tolbarLogin.subtitle = "$tipoBanco [${getVersion()}]"
        }
        mSnackBarCustom = CustomSnackBarCustom()
        mDialog = CustomAlertDialogCustom().progress(this, "Verificando seu login...")
        mDialog.hide()
    }

    private fun initViewModel() {
        Log.e("LOGIN", "initViewModel BASE URL = ${RetrofitClient.baseUrl} ")
        viewModel = ViewModelProvider(
            this,
            LoginViewModel.LoginViewModelFactory(LoginRepository())
        )[LoginViewModel::class.java]
        setObervable()
    }

    /**RESULTADOS DO VIEWMODEL -->*/
    private fun setObervable() {
        viewModel!!.loginSucess.observe(this) { token ->
            sharedPreferences.saveString(CustomSharedPreferences.TOKEN, token)
            val version = getVersion()
            val versionCurrent =
                sharedPreferences.getString(CustomSharedPreferences.VERSION_CURRENT)
            if (version != versionCurrent) {
                alertDailog.alertNative(
                    context = this,
                    title = "Melhorias da atualização ${getVersion()}",
                    message = "${getBullet()}Picking\n Ver volumes apontados e não apontados.\n${getBullet()}Separação\nFiltros por tipo de documento e transportadoras",
                    onClick = {
                        startActivity(token)
                    }
                )
            } else {
                startActivity(token)
            }
        }
        viewModel!!.errorLoginUser.observe(this) { message ->
            CustomMediaSonsMp3().somError(this)
            if (message == "USUARIO INVALIDO!") {
                binding.usuario.requestFocus()
                binding.usuario.shake {
                    toastError(this, message)
                }
            } else {
                vibrateExtension()
                binding.senha.requestFocus()
                binding.senha.shake {
                    toastError(this, message)
                }
            }
        }
        viewModel!!.mLoginErrorServ.observe(this) { message ->
            CustomMediaSonsMp3().somError(this)
            toastError(this, message.toString())
        }

        viewModel!!.mErrorAllShow.observe(this) { errorAll ->
            CustomMediaSonsMp3().somError(this)
            toastError(this, errorAll.toString())
        }
        viewModel!!.mProgressShow.observe(this) { progress ->
            if (progress) {
                mDialog.show()
            } else {
                mDialog.hide()
            }
        }
    }

    /**click button entrar -->*/
    private fun click() {
        binding.buttonLogin.setOnClickListener {
            saveUserShared()
            viewModel!!.getToken(
                binding.editUsuarioLogin.text.toString().trim(),
                binding.editSenhaLogin.text.toString().trim()
            )
        }
    }

    private fun saveUserShared() {
        val usuario = binding.editUsuarioLogin.text.toString().trim()
        val senha = binding.editSenhaLogin.text.toString().trim()
        sharedPreferences.saveString(CustomSharedPreferences.NAME_USER, usuario)
        sharedPreferences.saveString(CustomSharedPreferences.SENHA_USER, senha)
    }

    /**INICIA PROXIMA ACTIVITY -->*/
    private fun startActivity(token: String) {
        sharedPreferences.saveString(CustomSharedPreferences.VERSION_CURRENT, value = getVersion())
        ServiceApi.TOKEN = token
        val intent = Intent(this, ArmazensActivity::class.java)
        mResponseBack.launch(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**QUANDO CAMPO USER E SENHA NAO FOR VAZIO HABILITA O BUTTON DE LOGIN -->*/
    private fun validButton() {
        binding.editSenhaLogin.changedEditText { editSenha(binding.editSenhaLogin.text.toString()) }
        binding.editUsuarioLogin.changedEditText { editUser(binding.editUsuarioLogin.text.toString()) }
    }

    private fun editUser(s: String) {
        binding.buttonLogin.isEnabled =
            binding.editSenhaLogin.text!!.isNotEmpty() && s.isNotEmpty()
    }

    private fun editSenha(s: String) {
        binding.buttonLogin.isEnabled =
            binding.editUsuarioLogin.text!!.isNotEmpty() && s.isNotEmpty()
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
            binding.editUsuarioLogin.setText("")
            binding.editSenhaLogin.setText("")
            mShow.dismiss()
        }
        mBindingdialog.buttonNao.setOnClickListener {
            mShow.dismiss()
            val usuario = sharedPreferences.getString(CustomSharedPreferences.NAME_USER)
            val senha = sharedPreferences.getString(CustomSharedPreferences.SENHA_USER)
            if (usuario.isNullOrEmpty() || senha.isNullOrEmpty()) {
                mSnackBarCustom.snackBarPadraoSimplesBlack(
                    binding.layoutLoginTest,
                    "Ops...Faça o login novamente!"
                )
            } else {
                mShow.dismiss()
                viewModel!!.getToken(usuario, senha)
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
            if (binding.editUsuarioFiltrar.text.toString().trim().lowercase() == mSenhaUserAcesso
                && binding.editSenhaFiltrar.text.toString().trim().lowercase() == mSenhaAcesso
            ) {
                CustomMediaSonsMp3().somClick(this)
                ChangedBaseUrlDialog().show(supportFragmentManager, "BASE_URL")
                viewModel = null
                mShow.dismiss()
            } else {
                vibrateExtension(500)
                alertDailog.alertMessageErrorCancelFalse(
                    this,
                    "usuário ou senha inválidos"
                )
            }
        }
        binding.buttonClose.setOnClickListener {
            mShow.dismiss()
        }
    }

    /**LIMPA OS EDITS -->*/
    private fun clearEdits() {
        binding.editSenhaLogin.text!!.clear()
        binding.editUsuarioLogin.text!!.clear()
        binding.editUsuarioLogin.requestFocus()
        showKeyExtensionActivity(binding.editUsuarioLogin)
    }

    /**RETORNO DA BASEURL SELECIONADA NO DIALOG -->*/
    override fun sendBaseDialog(base: String, title: String) {
        clearEdits()
        sharedPreferences.saveString("TIPO_BANCO", title)
        sharedPreferences.saveString("BASE_URL", base)
        RetrofitClient.baseUrl = base
        binding.tolbarLogin.subtitle = "$title [${getVersion()}]"
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
                binding.root,
                "Clique novamente para fechar o aplicativo!"
            )
        }
    }
}