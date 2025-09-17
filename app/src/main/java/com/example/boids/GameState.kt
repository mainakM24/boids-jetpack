package com.example.boids

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

data class GameState(
    val boids : List<Boid> = emptyList(),
    val boidCount : Int = 100,
    val screenWidth : Float = 0f,
    val screenHeight: Float = 0f
) {
    fun init (width : Float, height : Float) : GameState {
        if (boids.isNotEmpty()) return this;
        val randomBoids = List(boidCount) {
            Boid (
                position = Offset(
                    x = Random.nextInt(0, width.toInt()).toFloat(),
                    y = Random.nextInt(0, height.toInt()).toFloat()
                ),
                velocity = Offset(
                    x = Random.nextInt(-120, 120).toFloat(),
                    y = Random.nextInt(-120, 120).toFloat()
                )
            )
        }

        return copy(
            boids = randomBoids,
            screenWidth = width,
            screenHeight = height
        )
    }

    fun update(dt: Float) : GameState {
        val updatedBoids = boids.map {boid -> boid.moveBoid( boids, dt, screenWidth, screenHeight)}
        return copy(boids = updatedBoids)
    }

}
