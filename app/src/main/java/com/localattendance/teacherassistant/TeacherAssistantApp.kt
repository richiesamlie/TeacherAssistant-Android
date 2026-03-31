package com.localattendance.teacherassistant

import android.app.Application
import com.localattendance.teacherassistant.data.api.ApiClient

class TeacherAssistantApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TeacherAssistantApp
            private set
    }
}
