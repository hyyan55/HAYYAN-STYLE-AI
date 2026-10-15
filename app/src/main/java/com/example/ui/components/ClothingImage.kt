package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import java.io.File

@Composable
fun ClothingImage(
    imagePath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            imagePath.startsWith("res:") -> {
                val resName = imagePath.removePrefix("res:")
                val drawableId = when (resName) {
                    "ic_category_top" -> R.drawable.ic_category_top
                    "ic_category_bottom" -> R.drawable.ic_category_bottom
                    "ic_category_shoes" -> R.drawable.ic_category_shoes
                    "ic_category_jacket" -> R.drawable.ic_category_jacket
                    "ic_category_accessory" -> R.drawable.ic_category_accessory
                    else -> R.drawable.ic_category_top
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = drawableId),
                        contentDescription = contentDescription,
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            imagePath.isNotBlank() && File(imagePath).exists() -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(imagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Checkroom,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
