package com.muhammed.supabasejetpackcompose.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.muhammed.supabasejetpackcompose.ui.theme.*

@Composable
fun SplashScreen() {
    val transition = rememberInfiniteTransition(label = "splash")
    
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_alpha"
    )

    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(BackgroundDark, Color(0xFF1E1B4B), BackgroundDark)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha),
                tint = PrimaryLight
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "PREMIUM NOTES",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = TextPrimary,
                modifier = Modifier.alpha(alpha)
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            LinearProgressIndicator(
                modifier = Modifier
                    .width(150.dp)
                    .clip(MaterialTheme.shapes.small),
                color = PrimaryMain,
                trackColor = SurfaceLighter
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}