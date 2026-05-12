package com.nexuslauncher.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.nexuslauncher.core.TierResult
import kotlin.random.Random

/**
 * NexusBackground3D — Fundo ESTÁTICO espacial do Nexus Launcher.
 *
 * FASE 3.5: Sem partículas, sem threads de renderização, sem animações.
 * Corrige ConcurrentModificationException da versão animada anterior.
 *
 * Desenha um fundo espacial estático com:
 * - Gradiente radial profundo
 * - Estrelas estáticas pré-geradas
 * - Névoas procedurais estáticas
 */
class NexusBackground3D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val tierResult: TierResult? = null
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback {

    private val bgPaint     = Paint()
    private val starPaint   = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val nebulaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    // Estrelas estáticas pré-geradas — nunca modificadas após criação
    private val stars: List<Triple<Float, Float, Float>> = (0 until 220).map {
        Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2.2f + 0.4f)
    }

    // Dim/bright stars for variety
    private val starAlphas: List<Int> = stars.map { (rx, _, _) ->
        (80 + (rx * 175).toInt()).coerceIn(70, 255)
    }

    init {
        holder.addCallback(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
        }
    }

    override fun surfaceCreated(h: SurfaceHolder) {
        drawStaticBackground()
    }

    override fun surfaceChanged(h: SurfaceHolder, format: Int, w: Int, hh: Int) {
        drawStaticBackground()
    }

    override fun surfaceDestroyed(h: SurfaceHolder) {
        // Nenhum thread para parar — totalmente seguro
    }

    private fun drawStaticBackground() {
        val canvas = holder.lockCanvas() ?: return
        try {
            drawScene(canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawScene(canvas: Canvas) {
        val w  = canvas.width.toFloat()
        val h  = canvas.height.toFloat()
        val cx = w / 2f
        val cy = h / 2f

        // Fundo: gradiente radial do espaço profundo
        bgPaint.shader = RadialGradient(
            cx, cy, w * 0.9f,
            intArrayOf(Color.parseColor("#0D0A1A"), Color.parseColor("#05050A")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Névoas estáticas
        drawNebula(canvas, cx - w * 0.25f, cy - h * 0.3f, w * 0.35f, "#1E0040", 0x1E)
        drawNebula(canvas, cx + w * 0.30f, cy + h * 0.20f, w * 0.28f, "#001540", 0x18)
        drawNebula(canvas, cx - w * 0.05f, cy + h * 0.30f, w * 0.22f, "#001020", 0x14)
        drawNebula(canvas, cx + w * 0.10f, cy - h * 0.20f, w * 0.18f, "#0A0030", 0x10)

        // Estrelas estáticas pré-geradas
        stars.forEachIndexed { i, (rx, ry, size) ->
            starPaint.color = Color.argb(starAlphas[i], 255, 255, 255)
            canvas.drawCircle(rx * w, ry * h, size * 0.5f, starPaint)
        }
    }

    private fun drawNebula(canvas: Canvas, nx: Float, ny: Float, radius: Float, hex: String, alpha: Int) {
        val baseColor = Color.parseColor(hex)
        val withAlpha = (baseColor and 0x00FFFFFF) or (alpha shl 24)
        nebulaPaint.shader = RadialGradient(
            nx, ny, radius,
            intArrayOf(withAlpha, Color.TRANSPARENT),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawCircle(nx, ny, radius, nebulaPaint)
        nebulaPaint.shader = null
    }

    /**
     * Mantida para compatibilidade com SolarSystemScreen.
     * Na versão estática, não faz nada — sem threads ou partículas.
     */
    fun updateTier(result: TierResult?) {
        // Fundo estático — sem ajuste de densidade por tier
    }
}
