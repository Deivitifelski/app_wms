package com.documentos.wms_beirario.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.documentos.wms_beirario.databinding.CustomButtomProgressBinding

class CustomViewButtomProgress(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val binding =
        CustomButtomProgressBinding.inflate(LayoutInflater.from(context), this, false)

    init {

    }
}