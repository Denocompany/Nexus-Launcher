package com.nexuslauncher.render

import android.content.Context
import android.view.SurfaceHolder
import com.nexuslauncher.core.NexusTier
import com.nexuslauncher.core.TierProfile
import com.nexuslauncher.core.TierResult

/**
 * NexusRenderEngine — Motor de renderização modular do Nexus Launcher.
 *
 * Pipeline de fallback automático:
 *   Vulkan → OpenGL ES 3.0 → OpenGL ES 2.0 → Canvas 2D
 *
 * Fase 2: implementação completa com suporte a OpenGL ES e fallback Canvas.
 * Fase 3: integração nativa JNI para Vulkan full pipeline.
 */
class NexusRenderEngine(
    private val context: Context,
    private val tierResult: TierResult
) {

    enum class ActiveRenderer { VULKAN, OPENGL_ES3, OPENGL_ES2, CANVAS }

    private var activeRenderer: ActiveRenderer = ActiveRenderer.CANVAS
    private var glRenderer: NexusRendererGL?   = null
    private var vkRenderer: NexusRendererVK?   = null
    private val frameStats = NexusFrameStats()

    val currentRenderer get() = activeRenderer
    val stats           get() = frameStats

    /** Inicializa o melhor renderizador disponível para o Tier. */
    fun initialize(holder: SurfaceHolder): Boolean {
        val preset = TierProfile.presetFor(tierResult.tier)

        activeRenderer = when {
            tierResult.hasVulkan && tierResult.tier.isHighEnd -> {
                vkRenderer = NexusRendererVK(context)
                if (vkRenderer!!.init(holder)) ActiveRenderer.VULKAN
                else tryOpenGL(holder, preset)
            }
            tierResult.tier.level >= 2 -> tryOpenGL(holder, preset)
            else -> {
                activeRenderer = ActiveRenderer.CANVAS
                ActiveRenderer.CANVAS
            }
        }

        return true
    }

    private fun tryOpenGL(holder: SurfaceHolder, preset: Any): ActiveRenderer {
        glRenderer = NexusRendererGL(context, tierResult)
        return if (glRenderer!!.init(holder)) {
            if (glRenderer!!.isES3Supported) ActiveRenderer.OPENGL_ES3
            else ActiveRenderer.OPENGL_ES2
        } else {
            ActiveRenderer.CANVAS
        }
    }

    /** Renderiza um frame. Chame a cada vsync. */
    fun renderFrame() {
        val start = System.nanoTime()

        when (activeRenderer) {
            ActiveRenderer.VULKAN      -> vkRenderer?.renderFrame()
            ActiveRenderer.OPENGL_ES3,
            ActiveRenderer.OPENGL_ES2  -> glRenderer?.renderFrame()
            ActiveRenderer.CANVAS      -> { /* Canvas rendering handled by NexusBackground3D */ }
        }

        val elapsed = (System.nanoTime() - start) / 1_000_000f
        frameStats.recordFrame(elapsed)
    }

    /** Libera recursos ao destruir a surface. */
    fun destroy() {
        glRenderer?.destroy()
        vkRenderer?.destroy()
        glRenderer = null
        vkRenderer = null
    }

    fun getDebugInfo(): String = buildString {
        appendLine("=== NexusRenderEngine Debug ===")
        appendLine("Renderer : $activeRenderer")
        appendLine("Tier     : ${tierResult.tier.label}")
        appendLine("Vulkan   : ${tierResult.hasVulkan}")
        appendLine("FPS avg  : ${String.format("%.1f", frameStats.averageFps)}")
        appendLine("Frame ms : ${String.format("%.2f", frameStats.averageFrameMs)}")
        appendLine("Frames   : ${frameStats.totalFrames}")
    }
}
