package com.documentos.wms_beirario.ui.rfid_recebimento.detalhesEpc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ActivityDetalheCodigoEpcBinding
import com.squareup.picasso.Picasso

class DetalheCodigoEpcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheCodigoEpcBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheCodigoEpcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setImagem()
    }

    private fun setImagem() {
        val imageUrl = "https://images.tcdn.com.br/img/img_prod/1085400/tira_de_couro_sintetico_30mm_rolo_com_10_metros_preto_1007_1_56582ada6e81506208a488b900343e4d.jpeg"
        Picasso.get()
            .load(imageUrl)
            .into(binding.imageEpc)
    }

}