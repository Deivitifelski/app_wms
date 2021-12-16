@file:Suppress("DEPRECATION")

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivityPopImpressorasBinding
import com.documentos.wms_beirario.ui.configuracoes.PrinterConnection


class PopImpressorasActivity : AppCompatActivity() {
    private val coletorPrinterConnection = PrinterConnection()
    private lateinit var mBinding: ActivityPopImpressorasBinding

    companion object {
        var sessionBluetooth = ""
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPopImpressorasBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        this.supportActionBar?.hide()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width: Int = displayMetrics.widthPixels
        var height: Int = displayMetrics.heightPixels

        window.setLayout((width * 0.95).toInt(), (height * 0.37).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = 75

        window.attributes = params

        mBinding.btPopImpressoras.setOnClickListener {
            finish()
        }

        mBinding.spinnerBluetooth.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            coletorPrinterConnection.findPrinters(applicationContext)
        )

        mBinding.spinnerBluetooth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var listOfString =
                        mBinding.spinnerBluetooth.adapter.getItem(position).toString().split(" ")
                    if (listOfString[0] != "-Selecione") {
                        sessionBluetooth = listOfString[3]
                    } else
                        sessionBluetooth = ""
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }
}