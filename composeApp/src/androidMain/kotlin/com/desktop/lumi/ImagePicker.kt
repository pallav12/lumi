package com.desktop.lumi

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class ImagePicker(
    private val onLaunch: () -> Unit
) {
    actual fun pickImage() {
        onLaunch()
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (String) -> Unit): ImagePicker {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onImagePicked(it.toString())
        }
    }

    return remember {
        ImagePicker(
            onLaunch = { launcher.launch(arrayOf("image/*")) }
        )
    }
}