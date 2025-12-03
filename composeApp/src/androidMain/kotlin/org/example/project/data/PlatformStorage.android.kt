package org.example.project.data

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

lateinit var appContext: Context

fun initAppContext(context: Context) {
    appContext = context
}

actual fun createSettings(): Settings {
    val prefs = appContext.getSharedPreferences("keyboard_warrior_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(prefs)
}


