package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun GifAnimationHolder(
    source: String,
    modifier: Modifier = Modifier,
) {
    KamelImage(
        resource = asyncPainterResource(source),
        contentDescription = "",
        modifier = modifier,
        onLoading = {
            Box(modifier.size(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        },
        onFailure = {
            Box(modifier.size(200.dp), contentAlignment = Alignment.Center) {
                Text(it.message ?: "")
            }
        }
    )
}