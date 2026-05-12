package com.nexuslauncher.core

import android.app.ActivityManager
import android.content.Context
import android.os.Build

/**
 * HardwareDetector — detecta as capacidades de hardware do dispositivo.
 * Fase 1: esqueleto. A lógica completa (OpenGL ES, RAM, CPU cores) vem na Fase 2.
 */
object HardwareDetector {

    /** Retorna a quantidade de RAM total disponível em MB. */
    fun getTotalRamMb(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024 * 1024)
    }

    /** Retorna o número de núcleos de CPU disponíveis. */
    fun getCpuCores(): Int = Runtime.getRuntime().availableProcessors()

    /** Retorna a ABI primária do dispositivo. */
    fun getPrimaryAbi(): String = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"

    /** Retorna a versão do Android como inteiro (API level). */
    fun getApiLevel(): Int = Build.VERSION.SDK_INT
}
