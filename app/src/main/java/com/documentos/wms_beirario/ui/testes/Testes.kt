package com.documentos.wms_beirario.ui.testes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.documentos.wms_beirario.databinding.ActivitySpeedTestBinding

class Testes : AppCompatActivity(), CustomDialogFilter.callResult {

    private lateinit var mBinding: ActivitySpeedTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySpeedTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



        mBinding.buttonSearch1.setOnClickListener {
            CustomDialogFilter(MockCity.setupCity()).show(supportFragmentManager, "")
        }

        mBinding.buttonSearch2.setOnClickListener {
            val arrayCity = mutableListOf<CityMock>()
            arrayCity.add(CityMock(1, "1"))
            arrayCity.add(CityMock(2, "2"))
            arrayCity.add(CityMock(3, "3"))
            arrayCity.add(CityMock(4, "4"))
            arrayCity.add(CityMock(5, "5"))
            arrayCity.add(CityMock(6, "6"))
            arrayCity.add(CityMock(7, "7"))
            CustomDialogFilter(arrayCity).show(supportFragmentManager, "")
        }


    }

    override fun result(city: CityMock) {
        mBinding.name.text = "Nome: ${city.city}"
        mBinding.numberid.text = "Id: ${city.id}"
    }


}

data class CityMock(val id: Int, val city: String)
class MockCity() {
    companion object {
        fun setupCity(): MutableList<CityMock> {
            val arrayCity = mutableListOf<CityMock>()
            arrayCity.add(CityMock(1, "Campo Bom"))
            arrayCity.add(CityMock(2, "Sapiranga"))
            arrayCity.add(CityMock(3, "Parobe"))
            arrayCity.add(CityMock(4, "Esteio"))
            arrayCity.add(CityMock(5, "Estrela"))
            arrayCity.add(CityMock(6, "Dois Irmao"))
            arrayCity.add(CityMock(7, "Porto alegre"))
            arrayCity.add(CityMock(8, "Novo hamburgo"))
            arrayCity.add(CityMock(9, "Potigua"))
            arrayCity.add(CityMock(10, "Pirituba"))
            arrayCity.add(CityMock(11, "Campo Novo"))
            arrayCity.add(CityMock(12, "Igrejinha"))
            arrayCity.add(CityMock(13, "Alegrete"))
            arrayCity.add(CityMock(14, "Torres"))
            arrayCity.add(CityMock(15, "Tramandai"))
            arrayCity.add(CityMock(16, "Bom jesus"))
            arrayCity.add(CityMock(17, "Campo Belo"))
            arrayCity.add(CityMock(18, "Goiania"))
            arrayCity.add(CityMock(19, "Joao Pessoa"))
            arrayCity.add(CityMock(20, "Arroio do tigre"))
            arrayCity.add(CityMock(21, "Harmonia"))
            arrayCity.add(CityMock(22, "Paraiba"))
            arrayCity.add(CityMock(23, "Varginia"))
            arrayCity.add(CityMock(24, "Nova Tramandai"))
            return arrayCity
        }
    }
}