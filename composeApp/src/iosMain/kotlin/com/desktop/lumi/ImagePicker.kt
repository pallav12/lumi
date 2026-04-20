package com.desktop.lumi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
import platform.Foundation.NSData
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

actual class ImagePicker(
    private val onLaunch: () -> Unit
) {
    actual fun pickImage() {
        onLaunch()
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (String) -> Unit): ImagePicker {
    return remember {
        ImagePicker(
            onLaunch = {
                val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                    ?: return@ImagePicker

                val delegate = object : NSObject(),
                    UIImagePickerControllerDelegateProtocol,
                    UINavigationControllerDelegateProtocol {

                    override fun imagePickerController(
                        picker: UIImagePickerController,
                        didFinishPickingMediaWithInfo: Map<Any?, *>
                    ) {
                        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage
                            ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

                        image?.let {
                            // Save image to temporary directory and return the path
                            val imageData = UIImageJPEGRepresentation(it, 0.8)
                            if (imageData != null) {
                                val fileName = "lumi_anchor_${NSUUID().UUIDString}.jpg"
                                val filePath = NSTemporaryDirectory() + fileName
                                imageData.writeToFile(filePath, atomically = true)
                                onImagePicked(filePath)
                            }
                        }

                        picker.dismissViewControllerAnimated(true, completion = null)
                    }

                    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                        picker.dismissViewControllerAnimated(true, completion = null)
                    }
                }

                val pickerController = UIImagePickerController().apply {
                    sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                    this.delegate = delegate
                }

                rootViewController.presentViewController(pickerController, animated = true, completion = null)
            }
        )
    }
}
