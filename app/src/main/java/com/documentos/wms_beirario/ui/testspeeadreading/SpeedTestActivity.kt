package com.documentos.wms_beirario.ui.testspeeadreading

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.documentos.wms_beirario.databinding.ActivitySpeedTestBinding
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class SpeedTestActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySpeedTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySpeedTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        UIUtil.hideKeyboard(this)
        mBinding.edit01.requestFocus()

        mBinding.edit01.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit01.hint = cod
                mBinding.edit02.requestFocus()
            }
        }

        mBinding.edit02.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit02.hint = cod
                mBinding.edit03.requestFocus()
            }
        }

        mBinding.edit03.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit03.hint = cod
                mBinding.edit04.requestFocus()
            }
        }

        mBinding.edit04.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit04.hint = cod
                mBinding.edit05.requestFocus()
            }
        }

        mBinding.edit05.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit05.hint = cod
                mBinding.edit06.requestFocus()
            }
        }

        mBinding.edit06.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit06.hint = cod
                mBinding.edit07.requestFocus()
            }
        }

        mBinding.edit07.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit07.hint = cod
                mBinding.edit08.requestFocus()
            }
        }

        mBinding.edit08.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit08.hint = cod
                mBinding.edit09.requestFocus()
            }
        }

        mBinding.edit09.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit09.hint = cod
                mBinding.edit10.requestFocus()
            }
        }

        mBinding.edit10.addTextChangedListener { cod ->
            if (cod!!.isNotEmpty()) {
                mBinding.edit10.hint = cod
                mBinding.edit01.requestFocus()
            }
        }

    }
}