package com.nexuslauncher.render

import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.opengl.GLES30
import android.view.SurfaceHolder
import com.nexuslauncher.core.TierResult

/**
 * NexusRendererGL — Renderizador OpenGL ES 3.0 / 2.0.
 *
 * Gerencia o contexto EGL, compila shaders e executa o loop de renderização
 * do fundo 3D do Nexus Launcher.
 */
class NexusRendererGL(
    private val context: Context,
    private val tierResult: TierResult
) {

    private var eglDisplay: EGLDisplay? = null
    private var eglContext: EGLContext? = null
    private var eglSurface: EGLSurface? = null
    var isES3Supported = false
        private set

    private var particleProgram = 0
    private var bgProgram       = 0
    private var width  = 0
    private var height = 0

    /** Inicializa o contexto EGL e compila shaders. Retorna true se bem-sucedido. */
    fun init(holder: SurfaceHolder): Boolean = try {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        EGL14.eglInitialize(eglDisplay, version, 0, version, 1)

        val attribs = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE,    EGL14.EGL_WINDOW_BIT,
            EGL14.EGL_RED_SIZE,   8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE,  8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        EGL14.eglChooseConfig(eglDisplay, attribs, 0, configs, 0, 1, numConfigs, 0)

        // Try ES3 first, fall back to ES2
        val ctxAttribs3 = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE)
        eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT, ctxAttribs3, 0)
        isES3Supported = eglContext != EGL14.EGL_NO_CONTEXT

        if (!isES3Supported) {
            val ctxAttribs2 = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
            eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT, ctxAttribs2, 0)
        }

        val surfAttribs = intArrayOf(EGL14.EGL_NONE)
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0], holder.surface, surfAttribs, 0)
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)

        compileShaders()
        setupGL()
        true
    } catch (e: Exception) {
        false
    }

    fun renderFrame() {
        if (eglDisplay == null) return
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)

        GLES20.glClearColor(0.02f, 0.02f, 0.05f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        drawBackground()
        drawParticles()

        EGL14.eglSwapBuffers(eglDisplay, eglSurface)
    }

    private fun setupGL() {
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    private fun compileShaders() {
        bgProgram       = NexusShaderLoader.createProgram(BG_VERT, BG_FRAG)
        particleProgram = NexusShaderLoader.createProgram(PARTICLE_VERT, PARTICLE_FRAG)
    }

    private fun drawBackground() {
        if (bgProgram == 0) return
        GLES20.glUseProgram(bgProgram)
        // Full-screen quad for space background gradient
    }

    private fun drawParticles() {
        if (particleProgram == 0) return
        GLES20.glUseProgram(particleProgram)
        // Particle point sprites
    }

    fun destroy() {
        if (eglDisplay != null) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(eglDisplay, eglSurface)
            EGL14.eglDestroyContext(eglDisplay, eglContext)
            EGL14.eglTerminate(eglDisplay)
        }
        if (bgProgram != 0)       GLES20.glDeleteProgram(bgProgram)
        if (particleProgram != 0) GLES20.glDeleteProgram(particleProgram)
    }

    companion object {
        private const val BG_VERT = """
            attribute vec4 aPosition;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = aPosition;
                vTexCoord = (aPosition.xy + 1.0) * 0.5;
            }
        """
        private const val BG_FRAG = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform float uTime;
            void main() {
                vec3 topColor = vec3(0.02, 0.02, 0.08);
                vec3 botColor = vec3(0.01, 0.01, 0.03);
                vec3 color = mix(botColor, topColor, vTexCoord.y);
                float nebula = sin(vTexCoord.x * 3.14 + uTime * 0.1) * 0.02;
                gl_FragColor = vec4(color + nebula, 1.0);
            }
        """
        private const val PARTICLE_VERT = """
            attribute vec4 aPosition;
            attribute float aSize;
            attribute float aAlpha;
            varying float vAlpha;
            void main() {
                gl_Position = aPosition;
                gl_PointSize = aSize;
                vAlpha = aAlpha;
            }
        """
        private const val PARTICLE_FRAG = """
            precision mediump float;
            varying float vAlpha;
            uniform vec3 uColor;
            void main() {
                vec2 coord = gl_PointCoord - vec2(0.5);
                float dist = length(coord);
                if (dist > 0.5) discard;
                float alpha = (1.0 - dist * 2.0) * vAlpha;
                gl_FragColor = vec4(uColor, alpha);
            }
        """
    }
}
