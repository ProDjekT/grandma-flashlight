package com.example.grandmaflashlight

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var cameraIdWithFlash: String? = null
    private var torchOn: Boolean = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableFlashlightButton()
        } else {
            Toast.makeText(
                this,
                R.string.camera_permission_required,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraIdWithFlash = findCameraIdWithFlash()

        val button = findViewById<Button>(R.id.button_flashlight)

        if (cameraIdWithFlash == null) {
            button.isEnabled = false
            button.text = getString(R.string.flash_not_available)
            return
        }

        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> enableFlashlightButton()
            else -> permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun findCameraIdWithFlash(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun enableFlashlightButton() {
        val button = findViewById<Button>(R.id.button_flashlight)
        button.isEnabled = true
        button.setOnClickListener { toggleFlashlight() }
        updateButtonState(button)
    }

    private fun toggleFlashlight() {
        val id = cameraIdWithFlash ?: return
        torchOn = !torchOn
        try {
            cameraManager.setTorchMode(id, torchOn)
            updateButtonState(findViewById(R.id.button_flashlight))
        } catch (e: Exception) {
            torchOn = !torchOn
            Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonState(button: Button) {
        if (torchOn) {
            button.text = getString(R.string.tap_to_turn_off)
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_on))
        } else {
            button.text = getString(R.string.tap_to_turn_on)
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_off))
        }
    }

    override fun onStop() {
        super.onStop()
        cameraIdWithFlash?.let { id ->
            try {
                cameraManager.setTorchMode(id, false)
                torchOn = false
            } catch (_: Exception) { }
        }
    }
}
