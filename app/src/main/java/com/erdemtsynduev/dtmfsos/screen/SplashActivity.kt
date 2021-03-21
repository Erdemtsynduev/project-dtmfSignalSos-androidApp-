package com.erdemtsynduev.dtmfsos.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class SplashActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestMultiplePermissions()
    }

    private fun requestMultiplePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.size == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMainScreen()
            } else {
                Toast.makeText(
                    this,
                    "Необходимо дать разрешение, для работы приложения",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}