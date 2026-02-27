package com.desktop.lumi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ImagePicker(
    private val onLaunch: () -> Unit
) {
    actual fun pickImage() {
        onLaunch()
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (String) -> Unit): ImagePicker {
    // Note: To fully implement iOS image picking, you will need to wrap UIImagePickerController.
    // This stub prevents KMP compilation errors for now.
    return remember {
        ImagePicker(
            onLaunch = { /* TODO: Implement iOS Photo Picker */ }
        )
    }
}