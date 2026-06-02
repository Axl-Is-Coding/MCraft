package com.briskstudio.mcraft.launcher.Backend

import android.content.Context
import android.content.Intent
import com.briskstudio.mcraft.UserData.Userdata
import com.briskstudio.mcraft.launcher.Backend.Api.Downloader
import com.briskstudio.mcraft.launcher.Backend.VersionHandler.VersionManager
import com.briskstudio.mcraft.launcher.Backend.VersionHandler.Data.MinecraftVersion
import com.briskstudio.mcraft.game.GameActivity
import kotlinx.coroutines.*

object Launcher {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentDownloadJob: Job? = null
    
    // ========== VERSION MANAGEMENT ==========
    
    fun getInstalledVersions(): List<MinecraftVersion> {
        return VersionManager.getInstalledVersions()
    }
    
    fun getAllVersions(): List<MinecraftVersion> {
        return VersionManager.getAllVersions()
    }
    
    fun getSelectedVersion(): MinecraftVersion? {
        val selectedId = VersionManager.getSelectedVersion()
        return getAllVersions().find { it.id == selectedId }
    }
    
    fun setSelectedVersion(version: MinecraftVersion) {
        VersionManager.setSelectedVersion(version.id)
    }
    
    // ========== DOWNLOAD VERSION ==========
    
    fun downloadVersion(
        version: MinecraftVersion,
        onProgress: (Float) -> Unit = {},
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        // Cancel any ongoing download
        currentDownloadJob?.cancel()
        
        currentDownloadJob = scope.launch {
            try {
                // Start download
                onProgress(0f)
                
                Downloader.downloadClientJar(
                    version = version.id,
                    onProgress = { progress ->
                        onProgress(progress)
                    },
                    onComplete = { success ->
                        if (success) {
                            // Mark as installed in UserData
                            Userdata.markVersionInstalled(version.id, version.name)
                            
                            // Update VersionManager cache
                            VersionManager.refreshVersions()
                            
                            onComplete(true, "Version ${version.id} installed successfully!")
                        } else {
                            onComplete(false, "Failed to download ${version.id}")
                        }
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Error: ${e.message}")
            }
        }
    }
    
    fun cancelDownload() {
        currentDownloadJob?.cancel()
        currentDownloadJob = null
    }
    
    // ========== UNINSTALL VERSION ==========
    
    fun uninstallVersion(
        version: MinecraftVersion,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            try {
                Downloader.deleteVersion(version.id) { success ->
                    if (success) {
                        Userdata.markVersionUninstalled(version.id)
                        VersionManager.refreshVersions()
                        onComplete(true, "Version ${version.id} uninstalled")
                    } else {
                        onComplete(false, "Failed to uninstall ${version.id}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Error: ${e.message}")
            }
        }
    }
    
    // ========== LAUNCH GAME ==========
    
    fun launchGame(context: Context) {
        val selectedVersion = getSelectedVersion()
        
        if (selectedVersion == null) {
            showToast(context, "No version selected")
            return
        }
        
        if (!selectedVersion.isInstalled) {
            showToast(context, "Version ${selectedVersion.id} is not installed. Please download first.")
            return
        }
        
        // Launch GameActivity
        val intent = Intent(context, GameActivity::class.java).apply {
            putExtra("VERSION_ID", selectedVersion.id)
            putExtra("VERSION_NAME", selectedVersion.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    // ========== REFRESH VERSIONS ==========
    
    fun refreshVersions(onComplete: (List<MinecraftVersion>) -> Unit = {}) {
        scope.launch {
            VersionManager.refreshVersions()
            onComplete(getAllVersions())
        }
    }
    
    // ========== CHECK INSTALLATION STATUS ==========
    
    fun isVersionInstalled(versionId: String): Boolean {
        return Userdata.isVersionInstalled(versionId)
    }
    
    // ========== SETTINGS ==========
    
    fun getSettings() = Userdata.getSettings()
    
    fun saveSettings(settings: com.briskstudio.mcraft.UserData.Data.SettingPrefs) {
        Userdata.saveSettings(settings)
    }
    
    // ========== UTILITIES ==========
    
    private fun showToast(context: Context, message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}