package org.example.project.utils

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContextProvider {
    lateinit var context: Context
}