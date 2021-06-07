package com.ennote.android

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButtonToggleGroup

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var themeSetting: MaterialButtonToggleGroup
    private lateinit var defaultButton: Button
    private lateinit var darkButton: Button
    private lateinit var lightButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        themeSetting = findViewById(R.id.theme_setting)
        defaultButton = findViewById(R.id.btn_default)
        darkButton = findViewById(R.id.btn_dark)
        lightButton = findViewById(R.id.btn_light)

        //获取偏好设置
        val settingsPreferences: SharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE)
        when (settingsPreferences.getString("dark_theme", "default")) {
            "default" -> defaultButton.isPressed = true
            "dark" -> darkButton.isPressed = true
            "light" -> lightButton.isPressed = true
        }

        themeSetting.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val theme = when (checkedId) {
                    R.id.btn_default -> {
                        settingsPreferences.edit().apply {
                            putString("dark_theme", "default")
                            apply()
                        }
                        "default"
                    }
                    R.id.btn_dark -> {
                        settingsPreferences.edit().apply {
                            putString("dark_theme", "dark")
                            apply()
                        }
                        "dark"
                    }
                    else -> {
                        settingsPreferences.edit().apply {
                            putString("dark_theme", "light")
                            apply()
                        }
                        "light"
                    }
                }
                setDarkTheme(theme)
            }
        }
    }
}

fun setDarkTheme(option: String) {
    val themePreferences = when (option) {
        "dark" -> AppCompatDelegate.MODE_NIGHT_YES
        "light" -> AppCompatDelegate.MODE_NIGHT_NO
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(themePreferences)
}
