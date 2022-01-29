package com.example.br_coletores.viewModels.scanner

import java.util.*

class ObservableObject private constructor() : Observable() {

    fun updateValue(data: Any) {
        synchronized(this) {
            setChanged()
            notifyObservers(data)
        }
    }

    companion object {
        val instance = ObservableObject()
    }
}