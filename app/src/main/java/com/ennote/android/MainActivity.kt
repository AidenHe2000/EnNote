package com.ennote.android

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity(), NoteListFragment.Callbacks, NoteFragment.Callbacks {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var cancellationSignal: CancellationSignal? = null

    private inner class AuthenticationCallback(private val noteId: UUID) :
        BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            notifyUser("Authentication Error: $errString")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            //验证通过跳转
            val fragment = NoteFragment.newInstance(noteId)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        toolbar.title = "Note List"
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.menu)
        }

        //菜单占屏幕68%
        navigationView.layoutParams.width = (resources.displayMetrics.widthPixels * 0.68).toInt()
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.password_generator -> {
                    startActivity(Intent(this@MainActivity, PasswordActivity::class.java))
                    false
                }
                else -> {
                    false
                }
            }
        }

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = NoteListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }

        checkBiometricSupport()
    }

    override fun onNoteSelected(noteId: UUID, isEncrypted: Boolean) {
        if (isEncrypted) {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Biometric Required")
                .setNegativeButton(
                    "Cancel", this.mainExecutor, { _, _ -> notifyUser("You cancelled") }
                )
                .build()
            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                AuthenticationCallback(noteId)
            )
        } else {
            val fragment = NoteFragment.newInstance(noteId)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDeleteNoteSelected(note: Note) {
        val fragment = NoteListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        //可以做撤销功能
        Snackbar.make(drawerLayout, "Note deleted", Snackbar.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return false
    }

    private fun notifyUser(message: String) {
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            // Intentionally left blank
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Biometric authentication disabled")
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("No permission for biometric")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true
        } else true
    }
}