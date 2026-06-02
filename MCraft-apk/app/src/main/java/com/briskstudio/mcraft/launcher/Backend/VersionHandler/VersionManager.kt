package com.briskstudio.mcraft.launcher.Backend.VersionHandler

import android.content.Context
import com.briskstudio.mcraft.UserData.Userdata
import com.briskstudio.mcraft.launcher.Backend.VersionHandler.Data.MinecraftVersion
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.net.URL

object VersionManager {
    
    private var versionCache: List<MinecraftVersion>? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // List of all available versions (from Mojang API)
    data class AvailableVersion(
        val id: String,
        val name: String,
        val releaseTime: String,
        val url: String
    )
    
    // ========== GET VERSIONS ==========
    
    fun getInstalledVersions(): List<MinecraftVersion> {
        val installed = Userdata.getInstalledVersions()
        return installed.versions.map { version ->
            MinecraftVersion(
                id = version.id,
                name = version.name,
                isInstalled = version.isInstalled,
                isRecommended = isRecommendedVersion(version.id),
                isLatest = isLatestVersion(version.id),
                isLegacy = isLegacyVersion(version.id)
            )
        }
    }
    
    fun getAllVersions(): List<MinecraftVersion> {
        // Combine installed + available
        val installedMap = getInstalledVersions().associateBy { it.id }
        val allIds = getAllVersionIds()
        
        return allIds.map { id ->
            installedMap[id] ?: MinecraftVersion(
                id = id,
                name = "Minecraft $id",
                isInstalled = false,
                isRecommended = isRecommendedVersion(id),
                isLatest = isLatestVersion(id),
                isLegacy = isLegacyVersion(id)
            )
        }
    }
    
    private fun getAllVersionIds(): List<String> {
        // Hardcoded for now (can fetch from Mojang API later)
        return listOf(
            "1.20.4", "1.20.3", "1.20.2", "1.20.1", "1.20",
            "1.19.4", "1.19.3", "1.19.2", "1.19.1", "1.19",
            "1.18.2", "1.18.1", "1.18",
            "1.17.1", "1.17",
            "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.16",
            "1.15.2", "1.15.1", "1.15",
            "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14",
            "1.12.2", "1.12.1", "1.12",
            "1.11.2", "1.11.1", "1.11",
            "1.10.2", "1.10.1", "1.10",
            "1.9.4", "1.9.3", "1.9.2", "1.9.1", "1.9",
            "1.8.9", "1.8.8", "1.8.7", "1.8.6", "1.8.5", "1.8.4", "1.8.3", "1.8.2", "1.8.1", "1.8",
            "1.7.10", "1.7.9", "1.7.8", "1.7.7", "1.7.6", "1.7.5", "1.7.4", "1.7.2"
        )
    }
    
    // ========== VERSION TAGS ==========
    
    private fun isRecommendedVersion(version: String): Boolean {
        val recommended = listOf("1.20.4", "1.18.2", "1.16.5", "1.12.2", "1.8.9", "1.7.10")
        return recommended.contains(version)
    }
    
    private fun isLatestVersion(version: String): Boolean {
        return version == "1.20.4"
    }
    
    private fun isLegacyVersion(version: String): Boolean {
        val legacy = listOf("1.8.9", "1.7.10")
        return legacy.contains(version)
    }
    
    // ========== INSTALL / UNINSTALL ==========
    
    fun installVersion(version: String, onProgress: (Float) -> Unit = {}, onComplete: (Boolean) -> Unit = {}) {
        scope.launch {
            try {
                // 1. Mark as installing in UI
                onProgress(0.1f)
                
                // 2. Get version directory
                val versionDir = Userdata.getVersionDir(version)
                val clientJar = Userdata.getClientJar(version)
                
                // 3. Download client.jar from Mojang
                val versionUrl = getVersionDownloadUrl(version)
                if (versionUrl != null) {
                    downloadFile(versionUrl, clientJar) { progress ->
                        onProgress(0.1f + (progress * 0.8f)) // 10-90% progress
                    }
                }
                
                // 4. Mark as installed in Userdata
                withContext(Dispatchers.Main) {
                    Userdata.markVersionInstalled(version, "Minecraft $version")
                    onProgress(1f)
                    onComplete(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }
    
    fun uninstallVersion(version: String, onComplete: (Boolean) -> Unit = {}) {
        try {
            // Delete version directory
            val versionDir = Userdata.getVersionDir(version)
            versionDir.deleteRecursively()
            
            // Mark as not installed
            Userdata.markVersionUninstalled(version)
            onComplete(true)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false)
        }
    }
    
    // ========== DOWNLOAD HELPERS ==========
    
    private fun getVersionDownloadUrl(version: String): String? {
        // Mojang version manifest URL
        val manifestUrl = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
        
        return try {
            val manifestJson = JSONObject(URL(manifestUrl).readText())
            val versionsArray = manifestJson.getJSONArray("versions")
            
            for (i in 0 until versionsArray.length()) {
                val versionObj = versionsArray.getJSONObject(i)
                if (versionObj.getString("id") == version) {
                    return versionObj.getString("url")
                }
            }
            null
        } catch (e: Exception) {
            // Fallback: hardcoded URLs for common versions
            when (version) {
                "1.20.4" -> "https://piston-data.mojang.com/v1/objects/8dd1a28015f51b1803213895158f8aa1b5d1d15b/client.jar"
                "1.18.2" -> "https://piston-data.mojang.com/v1/objects/3cf24a8694aca626f2371ea6b4767f57a771616f/client.jar"
                else -> null
            }
        }
    }
    
    private suspend fun downloadFile(url: String, dest: File, onProgress: (Float) -> Unit) {
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { input ->
                dest.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytes = 0
                    val contentLength = URL(url).openConnection().contentLength
                    
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead
                        if (contentLength > 0) {
                            onProgress(totalBytes.toFloat() / contentLength)
                        }
                    }
                }
            }
        }
    }
    
    // ========== LAUNCH GAME ==========
    
    fun launchVersion(version: String, context: Context) {
        val intent = android.content.Intent(context, com.briskstudio.mcraft.game.GameActivity::class.java)
        intent.putExtra("VERSION_ID", version)
        intent.putExtra("VERSION_NAME", "Minecraft $version")
        context.startActivity(intent)
    }
    
    // ========== SELECTED VERSION ==========
    
    private var _selectedVersion: String? = null
    
    fun getSelectedVersion(): String? {
        if (_selectedVersion == null) {
            _selectedVersion = Userdata.getSettings().lastPlayedVersion ?: "1.20.4"
        }
        return _selectedVersion
    }
    
    fun setSelectedVersion(version: String) {
        _selectedVersion = version
        val settings = Userdata.getSettings()
        settings.lastPlayedVersion = version
        Userdata.saveSettings(settings)
    }
    
    // Add to VersionManager.kt
    fun refreshVersions() {
        versionCache = null
        // Force reload
        getAllVersions()
    }
}