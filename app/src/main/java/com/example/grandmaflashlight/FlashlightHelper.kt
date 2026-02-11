package com.example.grandmaflashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

object FlashlightHelper {

    private const val PREFS_NAME = "flashlight_prefs"
    private const val KEY_TORCH_ON = "torch_on"

    fun getTorchState(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_TORCH_ON, false)
    }

    fun setTorchState(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_TORCH_ON, on)
            .apply()
    }

    fun findCameraIdWithFlash(context: Context): String? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Toggles torch and persists state. Returns new state, or null if no flash available / error.
     */
    fun toggleTorch(context: Context): Boolean? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = findCameraIdWithFlash(context) ?: return null
        val newState = !getTorchState(context)
        return try {
            cameraManager.setTorchMode(cameraId, newState)
            setTorchState(context, newState)
            newState
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Sets torch on or off. Returns true if successful.
     */
    fun setTorch(context: Context, on: Boolean): Boolean {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = findCameraIdWithFlash(context) ?: return false
        return try {
            cameraManager.setTorchMode(cameraId, on)
            setTorchState(context, on)
            true
        } catch (e: Exception) {
            false
        }
    }
}
