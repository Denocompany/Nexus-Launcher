package com.nexuslauncher.render

import android.opengl.GLES20

/**
 * NexusShaderLoader — Compilação e linking de shaders GLSL para OpenGL ES.
 *
 * Inclui shaders prontos para:
 *  - Fundo espacial com nebulosas (fragment shader procedural)
 *  - Partículas com bloom
 *  - Cubos com wireframe brilhante
 *  - Pós-processamento HDR (fase 3)
 */
object NexusShaderLoader {

    fun createProgram(vertSrc: String, fragSrc: String): Int {
        val vert = compileShader(GLES20.GL_VERTEX_SHADER, vertSrc)
        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
        if (vert == 0 || frag == 0) return 0

        val prog = GLES20.glCreateProgram()
        GLES20.glAttachShader(prog, vert)
        GLES20.glAttachShader(prog, frag)
        GLES20.glLinkProgram(prog)

        val status = IntArray(1)
        GLES20.glGetProgramiv(prog, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] == 0) {
            GLES20.glDeleteProgram(prog)
            return 0
        }
        GLES20.glDeleteShader(vert)
        GLES20.glDeleteShader(frag)
        return prog
    }

    private fun compileShader(type: Int, src: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) return 0
        GLES20.glShaderSource(shader, src)
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    // ── Built-in Shaders ────────────────────────────────────────────────────

    /** Shader de fundo espacial procedural com nebulosas animadas */
    val SPACE_BG_FRAG = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform float uTime;
        uniform vec2  uResolution;

        float hash(vec2 p) {
            return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
        }

        float noise(vec2 p) {
            vec2 i = floor(p);
            vec2 f = fract(p);
            f = f * f * (3.0 - 2.0 * f);
            return mix(mix(hash(i), hash(i+vec2(1,0)), f.x),
                       mix(hash(i+vec2(0,1)), hash(i+vec2(1,1)), f.x), f.y);
        }

        void main() {
            vec2 uv = vTexCoord;
            vec3 col = vec3(0.01, 0.01, 0.04);

            // Nebula layer 1 (purple)
            float n1 = noise(uv * 3.0 + uTime * 0.05);
            col += vec3(0.08, 0.0, 0.15) * n1 * n1;

            // Nebula layer 2 (blue)
            float n2 = noise(uv * 2.0 - uTime * 0.03 + vec2(1.5));
            col += vec3(0.0, 0.03, 0.12) * n2;

            // Vignette
            float vig = 1.0 - length(uv - 0.5) * 1.2;
            col *= max(0.0, vig);

            gl_FragColor = vec4(col, 1.0);
        }
    """.trimIndent()

    /** Shader de partículas com glow */
    val PARTICLE_GLOW_FRAG = """
        precision mediump float;
        varying float vAlpha;
        uniform vec3 uColor;

        void main() {
            vec2 coord = gl_PointCoord - vec2(0.5);
            float dist = length(coord) * 2.0;
            if (dist > 1.0) discard;
            float glow = exp(-dist * dist * 3.0);
            float core = 1.0 - smoothstep(0.0, 0.4, dist);
            gl_FragColor = vec4(uColor, (glow * 0.6 + core * 0.4) * vAlpha);
        }
    """.trimIndent()

    /** Vertex shader padrão para full-screen quad */
    val FULLSCREEN_VERT = """
        attribute vec4 aPosition;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = aPosition;
            vTexCoord = (aPosition.xy + 1.0) * 0.5;
        }
    """.trimIndent()
}
