package com.example.project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Reusable JetPack Compose Colored Gradient configuration
 */
@Composable
fun RandomGradientBackground(seed: Long, modifier: Modifier = Modifier) {
    // Using a seed here so a trip will always have the same gradient (by using trip id as a seed)

    // Generate the colours based on the seed
    val colors = remember(seed) {
        val random = Random(seed)
        val colour1 = Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f)
        val colour2 = Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f)
        Pair(colour1, colour2)
    }

    val (colour1, colour2) = colors

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(listOf(colour1, colour2)),
                alpha = 1f
            )
        }
    }
}