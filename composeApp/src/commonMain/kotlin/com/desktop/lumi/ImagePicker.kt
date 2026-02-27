package com.desktop.lumi

import androidx.compose.runtime.Composable

expect class ImagePicker {
    fun pickImage()
}

@Composable
expect fun rememberImagePicker(onImagePicked: (String) -> Unit): ImagePicker