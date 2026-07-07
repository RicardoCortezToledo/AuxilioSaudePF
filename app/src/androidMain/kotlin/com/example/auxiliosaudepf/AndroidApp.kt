package com.example.auxiliosaudepf

import android.app.Application
import android.content.Context

class AndroidApp : Application() {
    companion object {
        var context: Context? = null
            private set
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
