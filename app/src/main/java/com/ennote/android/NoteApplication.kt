package com.ennote.android

import android.app.Application

class NoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NoteRepository.initialize(this)
    }
}