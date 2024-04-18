package com.example.project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project.ui.theme.Purple

/**
 * Reusable JetPack Compose Button configuration
 */
@Composable
fun AddButton(
    onClickAction: () -> Unit,
    alpha: Float? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.75f)
            ) {
                drawCircle(color = Purple, radius = size.minDimension / 2, alpha = alpha ?: 0.7f)
            }
            IconButton(
                onClick = onClickAction // Use the provided lambda for onClick
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp).padding(bottom = 1.dp, end = 1.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
