package com.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project.ui.theme.Shapes

@Composable
fun Pill(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Blue,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor, shape = Shapes.medium)
            .padding(8.dp)
    ) {
        content()
    }
}
