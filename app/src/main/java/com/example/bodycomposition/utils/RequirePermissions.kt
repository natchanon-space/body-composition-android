package com.example.bodycomposition.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object RequirePermissions {
    // All necessary permissions
    private val REQUIRED_PERMISSIONS =
        mutableListOf (
            Manifest.permission.CAMERA,
        ).apply {
            // CameraX permissions
            if (Build.VERSION.SDK_INT < 30) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            // Scale permissions
            if (Build.VERSION.SDK_INT in 23..30) {
                // android version 6-11
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
                add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (Build.VERSION.SDK_INT >= 31) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
                add(Manifest.permission.BLUETOOTH_SCAN)
            }
        }.toTypedArray()

    // TODO: Update dynamic permissions request
    fun requestPermissions(fragment: Fragment) {
        val activityResultLauncher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions())
            { permissions ->
                // Handle Permission granted/rejected
                var permissionGranted = true
                permissions.entries.forEach {
                    if (it.key in REQUIRED_PERMISSIONS && !it.value)
                        permissionGranted = false
                }
                if (!permissionGranted) {
                    Toast.makeText(fragment.requireContext(),
                        "Permission request denied",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // TODO: add something e.g. exit the app or ask again
                }
            }
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // Return true if all permission granted
//    fun allPermissionsGranted(fragment: Fragment) = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            fragment.requireContext(), it) == PackageManager.PERMISSION_GRANTED
//    }

    fun allPermissionsGranted(fragment: Fragment): Boolean {
        var check = true

        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "$permission granted")
            } else {
                check = false
                Log.d(TAG, "$permission denied")
            }
        }

        return check
    }

    private const val TAG = "RequirePermissions"
}
