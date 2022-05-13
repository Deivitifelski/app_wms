package com.documentos.wms_beirario.utils

import android.content.Context
import android.media.MediaPlayer
import com.documentos.wms_beirario.R

class CustomMediaSonsMp3 {

    fun somInit(context: Context): MediaPlayer? {
        var mPlay = MediaPlayer.create(context, R.raw.somicializacao)
        mPlay.start()
        return mPlay
    }

    fun somError(context: Context): MediaPlayer? {
        var mMediaError = MediaPlayer.create(context, R.raw.errorwindonsandroid)
        mMediaError.start()
        return mMediaError
    }

    fun somAlerta(context: Context): MediaPlayer? {
        var mMediaError = MediaPlayer.create(context, R.raw.erroandroid_curto)
        mMediaError.start()
        return mMediaError
    }

    fun somSucessReading(context: Context): MediaPlayer? {
        var mMediaError = MediaPlayer.create(context, R.raw.sucessreading)
        mMediaError.start()
        return mMediaError
    }

    fun somLeituraConcluida(context: Context): MediaPlayer? {
        var mMedialeitura = MediaPlayer.create(context, R.raw.somleituraconcuida)
        mMedialeitura.start()
        return mMedialeitura
    }

    fun somSucess(context: Context): MediaPlayer? {
        var mMediaSucess = MediaPlayer.create(context, R.raw.somsucess)
        mMediaSucess.start()
        return mMediaSucess
    }

    fun somClick(context: Context): MediaPlayer? {
        var mSomClick = MediaPlayer.create(context, R.raw.somclick)
        mSomClick.start()
        return mSomClick
    }

    fun somAtencao(context: Context): MediaPlayer? {
        var mSomAtencao = MediaPlayer.create(context, R.raw.somatencao)
        mSomAtencao.start()
        return mSomAtencao
    }

}