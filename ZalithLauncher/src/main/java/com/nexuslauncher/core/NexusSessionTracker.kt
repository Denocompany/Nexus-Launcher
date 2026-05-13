package com.nexuslauncher.core

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * NexusSessionTracker — Rastreia sessões de jogo para CHRONOS.
 *
 * Registra: início/fim, instância, FPS médio/mínimo/máximo,
 * CPU/RAM médios, crashes, e persiste em JSON.
 */
object NexusSessionTracker {

    data class SessionRecord(
        val id           : String,
        val instanceId   : String,
        val instanceName : String,
        val startTime    : Long,
        val endTime      : Long         = 0L,
        val fpsAvg       : Int          = 0,
        val fpsMin       : Int          = 999,
        val fpsMax       : Int          = 0,
        val cpuAvg       : Int          = 0,
        val ramPeakGb    : Float        = 0f,
        val tempAvg      : Float        = 0f,
        val crashCount   : Int          = 0,
        val stability    : Float        = 1f
    ) {
        val durationSeconds: Long get() = if (endTime > 0) (endTime - startTime) / 1000 else 0
        val durationLabel: String get() {
            val h = durationSeconds / 3600
            val m = (durationSeconds % 3600) / 60
            val s = durationSeconds % 60
            return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
        }
        val dateLabel: String get() {
            val now  = System.currentTimeMillis()
            val diff = now - startTime
            return when {
                diff < 86_400_000 -> "Hoje ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(startTime))}"
                diff < 172_800_000-> "Ontem ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(startTime))}"
                else              -> SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(startTime))
            }
        }
    }

    private val _sessions = MutableStateFlow<List<SessionRecord>>(emptyList())
    val sessions: StateFlow<List<SessionRecord>> = _sessions

    private val _activeSession = MutableStateFlow<SessionRecord?>(null)
    val activeSession: StateFlow<SessionRecord?> = _activeSession

    private var sessionFile: File? = null
    private val fpsBuffer  = mutableListOf<Int>()
    private val cpuBuffer  = mutableListOf<Int>()

    fun init(baseDir: File) {
        sessionFile = File(baseDir, "nexus_sessions.json")
        loadSessions()
    }

    private fun loadSessions() {
        val file = sessionFile ?: return
        if (!file.exists()) return
        runCatching {
            val arr = JSONArray(file.readText())
            val list = (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                SessionRecord(
                    id           = o.optString("id",     UUID.randomUUID().toString()),
                    instanceId   = o.optString("instanceId",   ""),
                    instanceName = o.optString("instanceName", "Instância"),
                    startTime    = o.optLong("startTime",  0L),
                    endTime      = o.optLong("endTime",    0L),
                    fpsAvg       = o.optInt("fpsAvg",   0),
                    fpsMin       = o.optInt("fpsMin",   0),
                    fpsMax       = o.optInt("fpsMax",   0),
                    cpuAvg       = o.optInt("cpuAvg",   0),
                    ramPeakGb    = o.optDouble("ramPeakGb", 0.0).toFloat(),
                    tempAvg      = o.optDouble("tempAvg",  0.0).toFloat(),
                    crashCount   = o.optInt("crashCount",  0),
                    stability    = o.optDouble("stability", 1.0).toFloat()
                )
            }
            _sessions.value = list.sortedByDescending { it.startTime }
        }
    }

    private fun saveSessions() {
        val file = sessionFile ?: return
        runCatching {
            val arr = JSONArray()
            _sessions.value.take(50).forEach { s ->  // Manter últimas 50 sessões
                arr.put(JSONObject().apply {
                    put("id",           s.id)
                    put("instanceId",   s.instanceId)
                    put("instanceName", s.instanceName)
                    put("startTime",    s.startTime)
                    put("endTime",      s.endTime)
                    put("fpsAvg",       s.fpsAvg)
                    put("fpsMin",       s.fpsMin)
                    put("fpsMax",       s.fpsMax)
                    put("cpuAvg",       s.cpuAvg)
                    put("ramPeakGb",    s.ramPeakGb)
                    put("tempAvg",      s.tempAvg)
                    put("crashCount",   s.crashCount)
                    put("stability",    s.stability)
                })
            }
            file.parentFile?.mkdirs()
            file.writeText(arr.toString(2))
        }
    }

    /** Inicia nova sessão. */
    fun startSession(instanceId: String, instanceName: String) {
        fpsBuffer.clear()
        cpuBuffer.clear()
        val session = SessionRecord(
            id           = UUID.randomUUID().toString(),
            instanceId   = instanceId,
            instanceName = instanceName,
            startTime    = System.currentTimeMillis()
        )
        _activeSession.value = session
    }

    /** Atualiza métricas da sessão ativa. */
    fun updateMetrics(metrics: NexusSystemMonitor.SystemMetrics) {
        if (_activeSession.value == null) return
        fpsBuffer.add(metrics.fpsCurrent)
        cpuBuffer.add(metrics.cpuPercent)
        // Mantém apenas os últimos 300 samples (5 min @ 1/s)
        if (fpsBuffer.size > 300) fpsBuffer.removeAt(0)
        if (cpuBuffer.size > 300) cpuBuffer.removeAt(0)
    }

    /** Finaliza sessão ativa e salva. */
    fun endSession(crashed: Boolean = false) {
        val active = _activeSession.value ?: return
        val fpsList = fpsBuffer.filter { it > 0 }
        val cpuList = cpuBuffer.filter { it >= 0 }
        val crashes = if (crashed) active.crashCount + 1 else active.crashCount
        val stability = if (crashes == 0) 1f else (1f - (crashes * 0.15f)).coerceIn(0f, 1f)

        val finished = active.copy(
            endTime    = System.currentTimeMillis(),
            fpsAvg     = if (fpsList.isEmpty()) 0 else fpsList.average().toInt(),
            fpsMin     = fpsList.minOrNull() ?: 0,
            fpsMax     = fpsList.maxOrNull() ?: 0,
            cpuAvg     = if (cpuList.isEmpty()) 0 else cpuList.average().toInt(),
            crashCount = crashes,
            stability  = stability
        )
        _sessions.value = listOf(finished) + _sessions.value
        _activeSession.value = null
        saveSessions()
    }

    /** Exporta sessões como TXT. */
    fun exportAsText(): String = buildString {
        appendLine("=== NEXUS LAUNCHER — RELATÓRIO DE SESSÕES ===")
        appendLine("Exportado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
        appendLine()
        _sessions.value.forEach { s ->
            appendLine("Data:       ${s.dateLabel}")
            appendLine("Instância:  ${s.instanceName}")
            appendLine("Duração:    ${s.durationLabel}")
            appendLine("FPS:        Média ${s.fpsAvg} | Mín ${s.fpsMin} | Máx ${s.fpsMax}")
            appendLine("CPU Médio:  ${s.cpuAvg}%")
            appendLine("RAM Pico:   ${"%.1f".format(s.ramPeakGb)} GB")
            appendLine("Crashes:    ${s.crashCount}")
            appendLine("Estabilidade: ${"%.0f".format(s.stability * 100)}%")
            appendLine("─────────────────────────────────────")
        }
    }

    /** Estatísticas globais. */
    data class GlobalStats(
        val totalSessions: Int   = 0,
        val totalHours   : Float = 0f,
        val avgFps       : Int   = 0,
        val bestFps      : Int   = 0,
        val totalCrashes : Int   = 0
    )

    fun getGlobalStats(): GlobalStats {
        val list = _sessions.value
        if (list.isEmpty()) return GlobalStats()
        return GlobalStats(
            totalSessions = list.size,
            totalHours    = list.sumOf { it.durationSeconds }.toFloat() / 3600f,
            avgFps        = list.filter { it.fpsAvg > 0 }.map { it.fpsAvg }.average().toInt().coerceAtLeast(0),
            bestFps       = list.maxOfOrNull { it.fpsMax } ?: 0,
            totalCrashes  = list.sumOf { it.crashCount }
        )
    }
}