package com.example.project.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable JetPack Compose Progress Indicator configuration
 */
@Composable
fun ColouredLinearProgressIndicator(progress: Float) {
    val startColor = Color.Red
    val endColor = Color.Green

    var interpolatedColor = Color.White

    println("PROGRESS: $progress")
    if (progress.isFinite() && (!progress.isNaN())) {

        interpolatedColor = lerp(startColor, endColor, progress)

        Column (
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 12.dp, bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp) // Adjust the height as needed
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = interpolatedColor,
                )
            }
        }
    } else {
        Column (
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 6.dp)
        ) {
            Text(
                text = "No tasks added yet...",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Left,
                color = Color.LightGray,
            )
        }
    }
}