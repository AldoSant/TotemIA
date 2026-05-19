package com.totem.ia.ui.components

import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class PermissionState(
    val permission: String,
    val isGranted: Boolean,
    val launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    fun launchPermissionRequest() {
        launcher.launch(permission)
    }
}

@Composable
fun rememberPermissionState(permission: String): PermissionState {
    val context = LocalContext.current
    var isGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        isGranted = granted
    }

    return remember(isGranted, launcher) {
        PermissionState(permission, isGranted, launcher)
    }
}
