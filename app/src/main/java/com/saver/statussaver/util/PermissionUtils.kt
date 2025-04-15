package com.saver.statussaver.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {

    const val PERMISSION_REQUEST_CODE = 100
    private val STORAGE_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            STORAGE_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun requestStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                activity.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                STORAGE_PERMISSIONS,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun requestStoragePermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${fragment.requireContext().packageName}")
                }
                fragment.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                fragment.startActivity(intent)
            }
        } else {
            fragment.requestPermissions(
                STORAGE_PERMISSIONS,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun shouldShowPermissionRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            !Environment.isExternalStorageManager()
        } else {
            STORAGE_PERMISSIONS.any {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }
        }
    }

    fun shouldShowPermissionRationale(fragment: Fragment): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            !Environment.isExternalStorageManager()
        } else {
            STORAGE_PERMISSIONS.any {
                fragment.shouldShowRequestPermissionRationale(it)
            }
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun isPermissionGranted(
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        return permissions.zip(grantResults.toTypedArray())
            .all { (_, result) -> result == PackageManager.PERMISSION_GRANTED }
    }
}
