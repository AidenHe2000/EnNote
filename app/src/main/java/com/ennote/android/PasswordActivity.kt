package com.ennote.android

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial

class PasswordActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var resultPassword: TextView
    private lateinit var regenerateButton: Button
    private lateinit var copyButton: Button
    private lateinit var currentLength: TextView
    private lateinit var passwordLength: SeekBar
    private lateinit var lowercaseSwitch: SwitchMaterial
    private lateinit var uppercaseSwitch: SwitchMaterial
    private lateinit var numberSwitch: SwitchMaterial
    private lateinit var symbolSwitch: SwitchMaterial

    private val passwordGenerator: Password = Password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        toolbar = findViewById(R.id.password_toolbar)
        setSupportActionBar(toolbar)

        resultPassword = findViewById(R.id.result_password)
        regenerateButton = findViewById(R.id.regenerate_button)
        copyButton = findViewById(R.id.copy_password)
        currentLength = findViewById(R.id.current_password_length)
        passwordLength = findViewById(R.id.password_length)
        lowercaseSwitch = findViewById(R.id.lowercase_switch)
        uppercaseSwitch = findViewById(R.id.uppercase_switch)
        numberSwitch = findViewById(R.id.number_switch)
        symbolSwitch = findViewById(R.id.symbol_switch)

        regenerateButton.setOnClickListener {
            resultPassword.text = passwordGenerator.getPassword()
        }

        copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("password generated", resultPassword.text)
            clipboard.setPrimaryClip(clip)
            Snackbar.make(copyButton, "Password Copied", Snackbar.LENGTH_SHORT).show()
        }

        currentLength.text = passwordLength.progress.toString()
        passwordLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //left blank
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentProgress: Int = passwordLength.progress
                currentLength.text = currentProgress.toString()
                passwordGenerator.length = currentProgress
                resultPassword.text = passwordGenerator.getPassword()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //left blank
            }
        })

        lowercaseSwitch.setOnClickListener {
            passwordGenerator.lowerCase = lowercaseSwitch.isChecked
            resultPassword.text = passwordGenerator.getPassword()
        }

        uppercaseSwitch.setOnClickListener {
            passwordGenerator.upperCase = uppercaseSwitch.isChecked
            resultPassword.text = passwordGenerator.getPassword()
        }

        numberSwitch.setOnClickListener {
            passwordGenerator.number = numberSwitch.isChecked
            resultPassword.text = passwordGenerator.getPassword()
        }

        symbolSwitch.setOnClickListener {
            passwordGenerator.symbol = symbolSwitch.isChecked
            resultPassword.text = passwordGenerator.getPassword()
        }
    }

    override fun onStop() {
        super.onStop()
        passwordGenerator.reset()
    }
}