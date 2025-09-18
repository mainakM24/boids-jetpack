package com.example.boids

import androidx.compose.ui.geometry.Offset

data class Boid(
    var position : Offset,
    var velocity : Offset,
    var maxSpeed : Float = 250f,
    var maxForce : Float = 200f,
    val localArea : Float = 400f,
    val separationRad : Float = 300f
) {
    fun moveBoid(boids: List<Boid>, dt : Float, width : Float, height : Float) : Boid  {
        var acceleration = Offset.Zero
        var newVelocity = Offset.Zero
        var newPosition = Offset.Zero
        val boundary = 150f
        val wallForce : Float = velocity.getDistance()


        val wall = wall(boundary, wallForce, width, height)
        val align = align(boids)
        val cohesion = cohesion(boids)
        val separation = separation(boids)

        acceleration += wall * 1f
        acceleration += align * 2f
        acceleration += cohesion * 1f
        acceleration += separation * 2f
        acceleration = limitMag(acceleration, maxForce)

        newVelocity = this.velocity + acceleration * dt
        newVelocity = newVelocity / newVelocity.getDistance() * maxSpeed

        newPosition = this.position + newVelocity * dt;

        return copy(
            position = newPosition,
            velocity = newVelocity,
        );

    }

    fun wall(boundary : Float, wallForce: Float, width : Float, height : Float) : Offset {
        var acceleration = Offset.Zero
        if (this.position.x < boundary) {
            val force = (boundary - this.position.x)  * wallForce
            acceleration += Offset(force, 0f)
        }
        if (width - this.position.x < boundary) {
            val force = (boundary - (width - this.position.x)) * wallForce
            acceleration += Offset(-force, 0f)

        }
        if (this.position.y < boundary) {
            val force = (boundary - this.position.y) * wallForce
            acceleration += Offset(0f, force)
        }
        if (height - this.position.y < boundary) {
            val force = (boundary - (height - this.position.y)) * wallForce
            acceleration += Offset(0f, -force)
        }

        return acceleration
    }

    fun align(boids: List<Boid>) : Offset {
        var steer = Offset.Zero
        var neighborCount = 0

        boids.forEach { other ->
            if (other != this && (other.position - this.position).getDistance() <= localArea) {
                steer += other.velocity
                neighborCount++
            }
        }

        if (neighborCount > 0) {
            steer /= neighborCount.toFloat()
            steer = steer / steer.getDistance() * maxSpeed
            steer = steer - this.velocity
            steer = limitMag(steer, maxForce)
        }
        return steer
    }


    fun cohesion(boids: List<Boid>) : Offset {
        var steer = Offset.Zero
        var neighborCount = 0

        boids.forEach { other ->
            if (other != this && (other.position - this.position).getDistance() <= localArea) {
                steer += other.position
                neighborCount++
            }
        }

        if (neighborCount > 0) {
            steer /= neighborCount.toFloat()
            steer = steer - this.position
            steer = steer / steer.getDistance() * maxSpeed
            steer = steer - this.velocity
            steer = limitMag(steer, maxForce)
        }

        return steer
    }


    fun separation(boids: List<Boid>) : Offset {
        var steer = Offset.Zero

        boids.forEach { other ->
            val distance = (other.position - this.position).getDistance()
            if ((other != this) &&  (distance <= separationRad) && (distance != 0f)) {
                var diff = this.position - other.position
                diff = (diff / diff.getDistance()) / distance
                steer += diff
            }
        }

        if (steer != Offset.Zero) {
            steer = steer / steer.getDistance() * maxSpeed
            steer = steer - this.velocity
            steer = limitMag(steer, maxForce)
        }
        return steer
    }

    fun netAcceleration(boids : List<Boid>) : Offset{
        var acceleration = Offset.Zero
        var avgVelocity = Offset.Zero
        var avgPosition = Offset.Zero
        var avgSeparation = Offset.Zero
        var neighborCount = 0

        boids.forEach { other ->
            val difference = this.position - other.position
            val distance = difference.getDistance()
            if (other != this && distance <= localArea) {
                avgVelocity += other.velocity
                avgPosition += other.position
                avgSeparation += (difference / distance) / distance
                neighborCount++
            }
        }
        if (neighborCount > 0) {
            avgVelocity /= neighborCount.toFloat()
            avgVelocity = avgVelocity / avgVelocity.getDistance() * maxSpeed

            avgPosition /= neighborCount.toFloat()
            avgPosition -= this.position
            avgPosition = avgPosition / avgPosition.getDistance() * maxSpeed

            avgSeparation = avgSeparation / avgSeparation.getDistance() * maxSpeed

            avgSeparation -= this.velocity
            avgVelocity -= this.velocity
            avgPosition -= this.velocity

            avgPosition = limitMag(avgPosition, maxForce)
            avgVelocity = limitMag(avgVelocity, maxForce)
            avgSeparation = limitMag(avgSeparation, maxForce)

            acceleration = avgVelocity + avgPosition + avgSeparation * 2f
        }
        return acceleration
    }

    fun limitMag(vector : Offset, maxMag: Float) : Offset {

        val mag = vector.getDistance()
        return if (mag > maxMag) {
            (vector / mag) * maxMag
        } else {
            vector
        }
    }

}
