package com.nexuslauncher.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipFile

/**
 * NexusModManager — Gerencia mods reais de uma instância.
 *
 * Lê metadata real de: fabric.mod.json, mods.toml, quilt.mod.json
 * Ativa/desativa movendo entre /mods e /mods.disabled
 * Persiste estado via JSON por instância
 */
object NexusModManager {

    data class NexusMod(
        val id          : String,
        val name        : String,
        val version     : String,
        val description : String = "",
        val author      : String = "Desconhecido",
        val loader      : String = "unknown",  // fabric, forge, quilt, neoforge
        val mcVersionReq: String = "*",
        val fileName    : String = "",
        val filePath    : String = "",
        val isEnabled   : Boolean = true,
        val fileSizeKb  : Long    = 0L
    )

    private val _mods    = MutableStateFlow<List<NexusMod>>(emptyList())
    val mods: StateFlow<List<NexusMod>> = _mods

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /** Carrega mods reais da pasta /mods de uma instância. */
    suspend fun loadMods(instanceDir: String) = withContext(Dispatchers.IO) {
        _loading.value = true
        val modsDir         = File(instanceDir, "mods")
        val modsDisabledDir = File(instanceDir, "mods.disabled")
        modsDir.mkdirs()
        modsDisabledDir.mkdirs()

        val allMods = mutableListOf<NexusMod>()

        // Mods ativos
        modsDir.listFiles { f -> f.extension == "jar" }?.forEach { jarFile ->
            allMods += parseModJar(jarFile, enabled = true)
        }

        // Mods desativados
        modsDisabledDir.listFiles { f -> f.extension == "jar" }?.forEach { jarFile ->
            allMods += parseModJar(jarFile, enabled = false)
        }

        _mods.value  = allMods.sortedWith(compareByDescending<NexusMod> { it.isEnabled }.thenBy { it.name })
        _loading.value = false
    }

    /** Lê metadata de um .jar de mod (fabric.mod.json / mods.toml / quilt.mod.json). */
    private fun parseModJar(jarFile: File, enabled: Boolean): NexusMod {
        var name        = jarFile.nameWithoutExtension
        var version     = "?"
        var description = ""
        var author      = "Desconhecido"
        var loader      = "unknown"
        var mcVersionReq = "*"

        try {
            ZipFile(jarFile).use { zip ->
                // Fabric
                zip.getEntry("fabric.mod.json")?.let { entry ->
                    val json = JSONObject(zip.getInputStream(entry).reader().readText())
                    name        = json.optString("name", name)
                    version     = json.optString("version", version)
                    description = json.optString("description", "")
                    author      = json.optJSONArray("authors")?.optString(0) ?: "Desconhecido"
                    loader      = "fabric"
                    mcVersionReq = json.optJSONObject("depends")?.optString("minecraft", "*") ?: "*"
                }

                // Forge / NeoForge (mods.toml)
                if (loader == "unknown") {
                    zip.getEntry("META-INF/mods.toml")?.let { entry ->
                        val toml = zip.getInputStream(entry).reader().readText()
                        name    = extractTomlField(toml, "displayName")?.trim('"') ?: name
                        version = extractTomlField(toml, "version")?.trim('"') ?: version
                        author  = extractTomlField(toml, "authors")?.trim('"') ?: "Desconhecido"
                        loader  = "forge"
                        description = extractTomlField(toml, "description")?.trim('"', '\n', ' ') ?: ""
                    }
                }

                // Quilt
                if (loader == "unknown") {
                    zip.getEntry("quilt.mod.json")?.let { entry ->
                        val json   = JSONObject(zip.getInputStream(entry).reader().readText())
                        val meta   = json.optJSONObject("quilt_loader")
                        val info   = meta?.optJSONObject("metadata")
                        name       = info?.optString("name", name) ?: name
                        version    = meta?.optString("version", version) ?: version
                        description= info?.optString("description", "") ?: ""
                        loader     = "quilt"
                        val contrib = info?.optJSONArray("contributors")
                        author     = contrib?.let {
                            val keys = it.getJSONObject(0).keys()
                            if (keys.hasNext()) keys.next() else "Desconhecido"
                        } ?: "Desconhecido"
                    }
                }
            }
        } catch (_: Exception) { /* jar corrompido ou sem metadata */ }

        return NexusMod(
            id          = jarFile.absolutePath,
            name        = name,
            version     = version,
            description = description,
            author      = author,
            loader      = loader,
            mcVersionReq= mcVersionReq,
            fileName    = jarFile.name,
            filePath    = jarFile.absolutePath,
            isEnabled   = enabled,
            fileSizeKb  = jarFile.length() / 1024
        )
    }

    private fun extractTomlField(toml: String, field: String): String? {
        val regex = Regex("""$field\s*=\s*["']?([^"'\n\r]+)["']?""")
        return regex.find(toml)?.groupValues?.getOrNull(1)
    }

    /** Ativa um mod (move de mods.disabled para mods). */
    suspend fun enableMod(filePath: String, instanceDir: String) = withContext(Dispatchers.IO) {
        val file    = File(filePath)
        val target  = File(instanceDir, "mods/${file.name}")
        if (file.exists()) file.renameTo(target)
        loadMods(instanceDir)
    }

    /** Desativa um mod (move de mods para mods.disabled). */
    suspend fun disableMod(filePath: String, instanceDir: String) = withContext(Dispatchers.IO) {
        val file    = File(filePath)
        val target  = File(instanceDir, "mods.disabled/${file.name}")
        if (file.exists()) file.renameTo(target)
        loadMods(instanceDir)
    }

    /** Remove um mod do disco. */
    suspend fun removeMod(filePath: String, instanceDir: String) = withContext(Dispatchers.IO) {
        File(filePath).delete()
        loadMods(instanceDir)
    }

    /** Instala um mod a partir de um arquivo .jar externo. */
    suspend fun installMod(sourceJar: File, instanceDir: String): Boolean = withContext(Dispatchers.IO) {
        if (!sourceJar.exists() || sourceJar.extension != "jar") return@withContext false
        val dest = File(instanceDir, "mods/${sourceJar.name}")
        sourceJar.copyTo(dest, overwrite = true)
        loadMods(instanceDir)
        true
    }

    /** Retorna label de quantidade de mods ativos. */
    fun activeCountLabel(): String {
        val count = _mods.value.count { it.isEnabled }
        return "$count ativo${if (count != 1) "s" else ""}"
    }

    /** Limpa estado (troca de instância). */
    fun clearMods() { _mods.value = emptyList() }
}