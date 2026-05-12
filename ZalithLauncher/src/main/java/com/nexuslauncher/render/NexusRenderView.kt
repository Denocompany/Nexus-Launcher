package com.nexuslauncher.render

import android.content.Context
import android.view.SurfaceView

/**
 * NexusRenderView — View de renderização nativa para o futuro renderizador OpenGL ES / C++.
 *
 * Fase 1: esqueleto apenas.
 * Na Fase 3, esta classe será conectada ao renderizador nativo via JNI,
 * recebendo callbacks de frame e eventos de input.
 *
 * Uso futuro esperado:
 *   val renderView = NexusRenderView(context)
 *   renderView.startRendering()
 *   renderView.stopRendering()
 */
class NexusRenderView(context: Context) : SurfaceView(context) {

    /** Inicia o loop de renderização nativo. (TODO: Fase 3 — JNI + OpenGL ES) */
    fun startRendering() {
        // TODO: chamar native startRender() via JNI
    }

    /** Para o loop de renderização e libera recursos. (TODO: Fase 3) */
    fun stopRendering() {
        // TODO: chamar native stopRender() via JNI
    }

    companion object {
        // Reservado para carregar a biblioteca nativa futuramente
        // init { System.loadLibrary("nexus_renderer") }
    }
}
