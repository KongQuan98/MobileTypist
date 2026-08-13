package org.example.project.utils

import androidx.compose.ui.graphics.ImageBitmap

expect fun shareText(text: String)
expect fun shareImage(image: ImageBitmap, text: String)
