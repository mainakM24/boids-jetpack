package com.example.boids

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlin.math.atan2

@Composable
fun GameScreen(modifier: Modifier) {

    val gameState = remember { mutableStateOf(GameState()) }
    val sprite : ImageBitmap = ImageBitmap.imageResource(R.drawable.boid);

    LaunchedEffect(Unit) {
        val gameShouldRun = true
        var lastTime = System.nanoTime();
        while (gameShouldRun) {
            val startTime = System.nanoTime();
            val dt = (startTime - lastTime) / 1_000_000_000.0f;
            lastTime = startTime;
            gameState.value = gameState.value.update(dt);
            delay(timeMillis = 16L)
        }
    }


    Canvas(modifier = modifier.fillMaxSize()) {
        if (gameState.value.screenWidth == 0f || gameState.value.screenHeight == 0f) {
            gameState.value = gameState.value.init(width = size.width, height = size.height);
        }

        val boids = gameState.value.boids;
        boids.map { boid ->
            val center = Offset(boid.position.x + sprite.width / 2, boid.position.y + sprite.height / 2)
            val angle = atan2(boid.velocity.y, boid.velocity.x) + Math.PI.toFloat() / 2f
            rotateRad(angle, center) {
                drawImage(
                    image = sprite,
                    dstSize = IntSize(width = sprite.width * 3, height = sprite.height * 3),
                    dstOffset = IntOffset(boid.position.x.toInt(), boid.position.y.toInt())
                )
            }
        }
    }
}