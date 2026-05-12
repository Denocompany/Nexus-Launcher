package com.nexuslauncher.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.nexuslauncher.core.NexusTier
import com.nexuslauncher.core.TierResult
import kotlin.math.cos
import kotlin.math.sin

/**
 * NexusBackground3D — Fundo 3D animado com partículas, parallax e nebulosas.
 *
 * Renderiza usando Canvas 2D com fallback automático para OpenGL ES.
 * Reage ao Tier: T1/T2 = denso, T3 = médio, T4/T5 = leve.
 * Reage ao toque: arrastar move a câmera, toque pulsa partículas.
 *
 * Uso em Compose: use AndroidView { NexusBackground3D(it, tierResult) }
 */
class NexusBackground3D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val tierResult: TierResult? = null
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback {

    private val particleCount get() = when (tierResult?.tier) {
        NexusTier.T1_ULTRA    -> 2000
        NexusTier.T2_ALTO     -> 1200
        NexusTier.T3_AVANCADO -> 600
        NexusTier.T4_MEDIO    -> 250
        else                  -> 80
    }

    private lateinit var particleSystem: NexusParticleSystem
    private var renderThread: Thread? = null
    private var running = false

    // Camera/parallax state
    private var camX = 0f
    private var camY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    // Paints
    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val cubePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1.5f
    }
    private val nebulaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val bgPaint     = Paint()

    init {
        holder.addCallback(this)
        particleSystem = NexusParticleSystem(particleCount)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
        }
    }

    override fun surfaceCreated(h: SurfaceHolder) { startRendering() }
    override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, hh: Int) {}
    override fun surfaceDestroyed(h: SurfaceHolder) { stopRendering() }

    private fun startRendering() {
        running = true
        renderThread = Thread {
            var lastTime = System.nanoTime()
            while (running) {
                val now = System.nanoTime()
                val dt  = (now - lastTime) / 1_000_000_000f
                lastTime = now

                particleSystem.update(dt)

                val canvas = holder.lockCanvas() ?: continue
                try { drawScene(canvas) }
                finally { holder.unlockCanvasAndPost(canvas) }

                Thread.sleep(16) // ~60fps
            }
        }.apply { isDaemon = true; start() }
    }

    private fun stopRendering() {
        running = false
        renderThread?.interrupt()
        renderThread = null
    }

    private fun drawScene(canvas: Canvas) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        val cx = w / 2f + camX * 0.3f
        val cy = h / 2f + camY * 0.3f

        // Background
        val bg = RadialGradient(cx, cy, w * 0.75f,
            intArrayOf(Color.parseColor("#0D0A1A"), Color.parseColor("#05050A")),
            null, Shader.TileMode.CLAMP)
        bgPaint.shader = bg
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Nebulae
        drawNebulae(canvas, w, h, cx, cy)

        // Particles
        val scaleX = w / 5f
        val scaleY = h / 5f

        particleSystem.getParticles().forEach { p ->
            val sx = cx + p.x * scaleX + camX * 0.1f
            val sy = cy + p.y * scaleY + camY * 0.1f

            when (p.type) {
                NexusParticleSystem.ParticleType.STAR -> {
                    starPaint.color = Color.argb(
                        (p.alpha * 255).toInt().coerceIn(0, 255),
                        255, 255, 255
                    )
                    canvas.drawCircle(sx, sy, p.size * 0.5f, starPaint)
                }
                NexusParticleSystem.ParticleType.DUST -> {
                    starPaint.color = Color.argb(
                        (p.alpha * 255).toInt().coerceIn(0, 255),
                        0, 229, 255
                    )
                    canvas.drawCircle(sx, sy, p.size * 0.4f, starPaint)
                }
                NexusParticleSystem.ParticleType.CUBE -> {
                    if (tierResult?.tier?.level ?: 0 >= 3) {
                        cubePaint.color = Color.argb(
                            (p.alpha * 255 * 0.6f).toInt().coerceIn(0, 255),
                            0, 120, 255
                        )
                        drawRotatedSquare(canvas, sx, sy, p.size, p.rotAngle, cubePaint)
                    }
                }
                NexusParticleSystem.ParticleType.NEBULA_POINT -> {
                    nebulaPaint.color = Color.argb(
                        (p.alpha * 255).toInt().coerceIn(0, 255),
                        120, 30, 255
                    )
                    canvas.drawCircle(sx, sy, p.size * 1.5f, nebulaPaint)
                }
                else -> {}
            }
        }
    }

    private fun drawNebulae(canvas: Canvas, w: Float, h: Float, cx: Float, cy: Float) {
        val t = System.currentTimeMillis() / 8000.0
        val offsets = listOf(
            Triple(cx + sin(t * 0.3).toFloat() * w * 0.15f, cy - h * 0.2f, "#1E0040"),
            Triple(cx - w * 0.2f + cos(t * 0.2).toFloat() * 30f, cy + h * 0.15f, "#001540"),
        )
        offsets.forEach { (nx, ny, hex) ->
            val rad = w * 0.25f
            val grad = RadialGradient(nx, ny, rad,
                intArrayOf(Color.parseColor(hex) and 0x22FFFFFF or 0x22000000, Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP)
            nebulaPaint.shader = grad
            nebulaPaint.color  = Color.parseColor(hex)
            canvas.drawCircle(nx, ny, rad, nebulaPaint)
            nebulaPaint.shader = null
        }
    }

    private fun drawRotatedSquare(canvas: Canvas, cx: Float, cy: Float, size: Float, angle: Float, paint: Paint) {
        canvas.save()
        canvas.translate(cx, cy)
        canvas.rotate(Math.toDegrees(angle.toDouble()).toFloat())
        canvas.drawRect(-size/2f, -size/2f, size/2f, size/2f, paint)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x; lastTouchY = event.y
                val px = (event.x - width / 2f) / (width / 5f)
                val py = (event.y - height / 2f) / (height / 5f)
                particleSystem.onTouch(px, py)
            }
            MotionEvent.ACTION_MOVE -> {
                camX += (event.x - lastTouchX) * 0.5f
                camY += (event.y - lastTouchY) * 0.5f
                camX = camX.coerceIn(-80f, 80f)
                camY = camY.coerceIn(-80f, 80f)
                lastTouchX = event.x; lastTouchY = event.y
            }
        }
        return true
    }
}
