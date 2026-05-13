package com.nexuslauncher.core

import android.content.Context
import com.movtery.zalithlauncher.feature.version.VersionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * NexusInstanceManager — Gerencia instâncias reais do Minecraft.
 *
 * Cada instância tem sua própria pasta isolada com toda a estrutura
 * do .minecraft: versions, libraries, assets, mods, resourcepacks,
 * shaderpacks, config, saves, logs.
 *
 * Persiste metadados em JSON no diretório base do launcher.
 */
object NexusInstanceManager {

    data class NexusInstance(
        val id          : String,
        val name        : String,
        val mcVersion   : String,
        val loader      : String,  // Vanilla, Fabric, Forge, Quilt, NeoForge
        val loaderVersion: String  = "",
        val ramMb       : Int      = 2048,
        val jvmArgs     : String   = "-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UnlockExperimentalVMOptions",
        val gameArgs    : String   = "",
        val iconName    : String   = "grass",
        val isFavorite  : Boolean  = false,
        val isLastUsed  : Boolean  = false,
        val dirPath     : String   = "",
        val isReady     : Boolean  = false,
        val createdAt   : Long     = System.currentTimeMillis()
    )

    private val _instances = MutableStateFlow<List<NexusInstance>>(emptyList())
    val instances: StateFlow<List<NexusInstance>> = _instances

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private var baseDir: File? = null
    private var metaFile: File? = null

    /** Inicializa com o diretório base do launcher. */
    fun init(context: Context) {
        val profileDir = try {
            ProfilePathManager.getCurrentProfile()?.profilePath?.let { File(it) }
        } catch (e: Exception) { null }

        val dir = profileDir ?: File(context.getExternalFilesDir(null), "NexusLauncher")
        baseDir  = dir
        metaFile = File(dir, "nexus_instances.json")
        dir.mkdirs()
        loadInstances()
    }

    /** Carrega instâncias do JSON persistido. */
    fun loadInstances() {
        val file = metaFile ?: return
        if (!file.exists()) {
            // Tenta importar instâncias do ZalithLauncher/PojavLauncher
            importFromExistingLauncher()
            return
        }
        runCatching {
            val json = JSONArray(file.readText())
            val list = mutableListOf<NexusInstance>()
            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                list.add(
                    NexusInstance(
                        id           = obj.optString("id", UUID.randomUUID().toString()),
                        name         = obj.optString("name", "Instância"),
                        mcVersion    = obj.optString("mcVersion", "1.21"),
                        loader       = obj.optString("loader", "Vanilla"),
                        loaderVersion= obj.optString("loaderVersion", ""),
                        ramMb        = obj.optInt("ramMb", 2048),
                        jvmArgs      = obj.optString("jvmArgs", "-XX:+UseG1GC"),
                        gameArgs     = obj.optString("gameArgs", ""),
                        iconName     = obj.optString("iconName", "grass"),
                        isFavorite   = obj.optBoolean("isFavorite", false),
                        isLastUsed   = obj.optBoolean("isLastUsed", false),
                        dirPath      = obj.optString("dirPath", ""),
                        isReady      = obj.optBoolean("isReady", false),
                        createdAt    = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            _instances.value = list
        }
    }

    /** Persiste lista de instâncias em JSON. */
    private fun saveInstances() {
        val file = metaFile ?: return
        runCatching {
            val json = JSONArray()
            _instances.value.forEach { inst ->
                json.put(JSONObject().apply {
                    put("id",            inst.id)
                    put("name",          inst.name)
                    put("mcVersion",     inst.mcVersion)
                    put("loader",        inst.loader)
                    put("loaderVersion", inst.loaderVersion)
                    put("ramMb",         inst.ramMb)
                    put("jvmArgs",       inst.jvmArgs)
                    put("gameArgs",      inst.gameArgs)
                    put("iconName",      inst.iconName)
                    put("isFavorite",    inst.isFavorite)
                    put("isLastUsed",    inst.isLastUsed)
                    put("dirPath",       inst.dirPath)
                    put("isReady",       inst.isReady)
                    put("createdAt",     inst.createdAt)
                })
            }
            file.parentFile?.mkdirs()
            file.writeText(json.toString(2))
        }
    }

    /** Tenta importar instâncias de launchers existentes (PojavLauncher, etc.). */
    private fun importFromExistingLauncher() {
        val candidates = listOf(
            File("/storage/emulated/0/games/PojavLauncher/.minecraft"),
            File("/storage/emulated/0/MCinaBox/.minecraft"),
            File("/storage/emulated/0/games/net.kdt.pojavlaunch/.minecraft")
        )
        val found = candidates.firstOrNull { it.exists() && it.isDirectory }
        if (found != null) {
            val inst = NexusInstance(
                id        = UUID.randomUUID().toString(),
                name      = "Importada (PojavLauncher)",
                mcVersion = detectMcVersion(found) ?: "desconhecida",
                loader    = "Vanilla",
                dirPath   = found.absolutePath,
                isReady   = true
            )
            _instances.value = listOf(inst)
            saveInstances()
        }
    }

    private fun detectMcVersion(mcDir: File): String? {
        val versionsDir = File(mcDir, "versions")
        if (!versionsDir.exists()) return null
        return versionsDir.listFiles()?.firstOrNull { it.isDirectory }?.name
    }

    /** Cria nova instância com estrutura de pastas completa. */
    suspend fun createInstance(
        name      : String,
        mcVersion : String,
        loader    : String,
        ramMb     : Int    = 2048
    ): NexusInstance = withContext(Dispatchers.IO) {
        val id      = UUID.randomUUID().toString()
        val base    = baseDir ?: File("/storage/emulated/0/NexusLauncher")
        val instDir = File(base, "instances/$id/.minecraft")
        createFolderStructure(instDir)

        val inst = NexusInstance(
            id        = id,
            name      = name,
            mcVersion = mcVersion,
            loader    = loader,
            ramMb     = ramMb,
            dirPath   = instDir.absolutePath,
            isReady   = false
        )
        _instances.value = _instances.value + inst
        saveInstances()
        inst
    }

    /** Cria estrutura completa de pastas .minecraft. */
    private fun createFolderStructure(mcDir: File) {
        val dirs = listOf(
            "versions", "libraries", "assets/indexes", "assets/objects",
            "mods", "mods.disabled", "resourcepacks", "shaderpacks",
            "config", "saves", "logs", "screenshots", "runtime"
        )
        dirs.forEach { File(mcDir, it).mkdirs() }
    }

    /** Duplica uma instância existente. */
    suspend fun duplicateInstance(id: String): NexusInstance? = withContext(Dispatchers.IO) {
        val original = _instances.value.firstOrNull { it.id == id } ?: return@withContext null
        val newId    = UUID.randomUUID().toString()
        val base     = baseDir ?: File("/storage/emulated/0/NexusLauncher")
        val newDir   = File(base, "instances/$newId/.minecraft")

        runCatching {
            File(original.dirPath).copyRecursively(newDir, overwrite = true)
        }

        val copy = original.copy(
            id        = newId,
            name      = "${original.name} (Cópia)",
            dirPath   = newDir.absolutePath,
            isLastUsed= false,
            createdAt = System.currentTimeMillis()
        )
        _instances.value = _instances.value + copy
        saveInstances()
        copy
    }

    /** Renomeia uma instância. */
    fun renameInstance(id: String, newName: String) {
        _instances.value = _instances.value.map {
            if (it.id == id) it.copy(name = newName) else it
        }
        saveInstances()
    }

    /** Remove uma instância e sua pasta. */
    suspend fun removeInstance(id: String): Boolean = withContext(Dispatchers.IO) {
        val inst = _instances.value.firstOrNull { it.id == id } ?: return@withContext false
        runCatching { File(inst.dirPath).parentFile?.deleteRecursively() }
        _instances.value = _instances.value.filter { it.id != id }
        saveInstances()
        true
    }

    /** Alterna favorito. */
    fun toggleFavorite(id: String) {
        _instances.value = _instances.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
        saveInstances()
    }

    /** Define instância como última usada. */
    fun setLastUsed(id: String) {
        _instances.value = _instances.value.map {
            it.copy(isLastUsed = it.id == id)
        }
        saveInstances()
    }

    /** Retorna instância mais recentemente usada. */
    fun getLastUsed(): NexusInstance? =
        _instances.value.firstOrNull { it.isLastUsed }
            ?: _instances.value.firstOrNull()

    /** Marca instância como pronta (download concluído). */
    fun markReady(id: String) {
        _instances.value = _instances.value.map {
            if (it.id == id) it.copy(isReady = true) else it
        }
        saveInstances()
    }

    /** Atualiza configurações de JVM/RAM de uma instância. */
    fun updateInstanceConfig(id: String, ramMb: Int, jvmArgs: String, gameArgs: String) {
        _instances.value = _instances.value.map {
            if (it.id == id) it.copy(ramMb = ramMb, jvmArgs = jvmArgs, gameArgs = gameArgs)
            else it
        }
        saveInstances()
    }

    /** Retorna o número de mods ativos de uma instância. */
    fun getModCount(id: String): Int {
        val inst = _instances.value.firstOrNull { it.id == id } ?: return 0
        return File(inst.dirPath, "mods").listFiles()
            ?.count { it.name.endsWith(".jar") } ?: 0
    }

    /** Muda o diretório base e migra instâncias (referências). */
    suspend fun changeBaseDir(context: Context, newPath: String) = withContext(Dispatchers.IO) {
        val newBase = File(newPath)
        newBase.mkdirs()
        baseDir  = newBase
        metaFile = File(newBase, "nexus_instances.json")
        saveInstances()
    }
}