package com.nexuslauncher.render

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * NexusParticleSystem — Sistema de partículas 3D para o fundo animado.
 *
 * Suporta:
 *  - Partículas de estrelas
 *  - Poeira cósmica
 *  - Cubos flutuantes (para efeito de profundidade)
 *  - Nebulosas procedurais
 *  - Colisão suave entre partículas próximas
 */
class NexusParticleSystem(
    val maxParticles: Int = 2000
) {

    data class Particle(
        var x: Float, var y: Float, var z: Float,
        var vx: Float, var vy: Float, var vz: Float,
        var size: Float,
        var alpha: Float,
        var r: Float, var g: Float, var b: Float,
        val type: ParticleType,
        var life: Float = 1f,
        var maxLife: Float = 1f,
        var rotAngle: Float = 0f,
        var rotSpeed: Float = 0f
    )

    enum class ParticleType { STAR, DUST, CUBE, NEBULA_POINT, ORBITAL_TRAIL }

    private val particles = ArrayList<Particle>(maxParticles)
    private var time = 0f

    init { spawn(maxParticles) }

    private fun spawn(count: Int) {
        repeat(count) {
            val type = when (Random.nextInt(10)) {
                in 0..5 -> ParticleType.STAR
                in 6..7 -> ParticleType.DUST
                8       -> ParticleType.CUBE
                else    -> ParticleType.NEBULA_POINT
            }
            particles.add(newParticle(type))
        }
    }

    private fun newParticle(type: ParticleType): Particle {
        val angle  = Random.nextFloat() * Math.PI.toFloat() * 2f
        val radius = Random.nextFloat() * 2f
        return when (type) {
            ParticleType.STAR -> Particle(
                x = (Random.nextFloat() - 0.5f) * 4f,
                y = (Random.nextFloat() - 0.5f) * 4f,
                z = Random.nextFloat() * -5f,
                vx = 0f, vy = 0f, vz = 0.001f,
                size  = Random.nextFloat() * 3f + 1f,
                alpha = Random.nextFloat() * 0.8f + 0.2f,
                r = 1f, g = 1f, b = 1f,
                type = type,
                maxLife = 1f,
                rotAngle = 0f, rotSpeed = 0f
            )
            ParticleType.DUST -> Particle(
                x = cos(angle) * radius, y = sin(angle) * radius,
                z = (Random.nextFloat() - 0.5f) * 2f,
                vx = (Random.nextFloat() - 0.5f) * 0.002f,
                vy = (Random.nextFloat() - 0.5f) * 0.002f,
                vz = 0f,
                size  = Random.nextFloat() * 2f + 0.5f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                r = 0f, g = 0.9f, b = 1f,
                type = type,
                maxLife = Random.nextFloat() * 0.5f + 0.5f
            )
            ParticleType.CUBE -> Particle(
                x = (Random.nextFloat() - 0.5f) * 3f,
                y = (Random.nextFloat() - 0.5f) * 3f,
                z = (Random.nextFloat() - 0.5f) * 2f,
                vx = (Random.nextFloat() - 0.5f) * 0.001f,
                vy = (Random.nextFloat() - 0.5f) * 0.001f,
                vz = 0f,
                size  = Random.nextFloat() * 12f + 6f,
                alpha = 0.15f,
                r = 0f, g = 0.5f, b = 1f,
                type = type,
                maxLife = 1f,
                rotSpeed = (Random.nextFloat() - 0.5f) * 0.02f
            )
            else -> Particle(
                x = cos(angle) * radius * 1.5f,
                y = sin(angle) * radius * 1.5f,
                z = (Random.nextFloat() - 0.5f) * 3f,
                vx = 0f, vy = 0f, vz = 0f,
                size  = Random.nextFloat() * 4f + 2f,
                alpha = Random.nextFloat() * 0.2f + 0.05f,
                r = 0.5f, g = 0.1f, b = 1f,
                type = type
            )
        }
    }

    /** Atualiza todas as partículas. Chame a cada frame com deltaTime em segundos. */
    fun update(dt: Float) {
        time += dt
        val it = particles.iterator()
        val dead = mutableListOf<ParticleType>()

        while (it.hasNext()) {
            val p = it.next()
            p.x += p.vx
            p.y += p.vy
            p.z += p.vz
            p.rotAngle += p.rotSpeed
            p.life -= dt * 0.05f

            // Boundary wrap
            if (p.x >  2.5f) p.x = -2.5f
            if (p.x < -2.5f) p.x =  2.5f
            if (p.y >  2.5f) p.y = -2.5f
            if (p.y < -2.5f) p.y =  2.5f
            if (p.z < -5f)   { p.z = 0f }

            if (p.life <= 0f && p.type != ParticleType.STAR) {
                it.remove()
                dead += p.type
            }
        }

        // Respawn dead particles
        dead.forEach { particles.add(newParticle(it)) }

        // Twinkling for stars
        particles.filter { it.type == ParticleType.STAR }.forEach {
            it.alpha = 0.3f + sin(time * 2f + it.x * 10f).toFloat() * 0.35f + 0.35f
        }
    }

    /** Reacts to touch: sends shockwave through nearby particles. */
    fun onTouch(touchX: Float, touchY: Float) {
        particles.forEach { p ->
            val dx = p.x - touchX
            val dy = p.y - touchY
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist < 0.5f && dist > 0.001f) {
                val force = (0.5f - dist) * 0.02f
                p.vx += (dx / dist) * force
                p.vy += (dy / dist) * force
                p.alpha = (p.alpha + 0.3f).coerceAtMost(1f)
            }
        }
    }

    fun getParticles(): List<Particle> = particles

    fun setDensity(count: Int) {
        while (particles.size > count) particles.removeAt(particles.lastIndex)
        while (particles.size < count) particles.add(newParticle(ParticleType.STAR))
    }
}
