package com.example.simplechoose.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class ThemeUtils {

    companion object {
        fun isDarkMode(config: Configuration): Boolean {
            val uiMode = config.uiMode and Configuration.UI_MODE_NIGHT_YES
            return uiMode == Configuration.UI_MODE_NIGHT_YES
        }

        fun isDarkMode(context: Context): Boolean {
            return isDarkMode(context.resources.configuration)
        }

        fun initTheme() {
            // 跟随系统
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            // 暗色模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            // 正常模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}