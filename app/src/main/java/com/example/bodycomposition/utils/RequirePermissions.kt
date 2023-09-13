package com.example.bodycomposition.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object RequirePermissions {
    // All necessary permissions
    val REQUIRED_PERMISSIONS =
        mutableListOf (
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

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
    fun allPermissionsGranted(fragment: Fragment) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            fragment.requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
}
