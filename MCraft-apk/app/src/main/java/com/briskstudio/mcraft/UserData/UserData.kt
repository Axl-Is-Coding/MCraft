package com.briskstudio.mcraft.UserData

import android.content.Context
import android.content.SharedPreferences
import com.briskstudio.mcraft.UserData.Data.InstalledVersions
import com.briskstudio.mcraft.UserData.Data.SettingPrefs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object Userdata {
    
    private lateinit var appContext: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var gson: Gson
    
    // Data directories
    lateinit var dataDir: File
    lateinit var versionsDir: File
    
    fun init(context: Context) {
        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences("mcraft_prefs", Context.MODE_PRIVATE)
        gson = Gson()
        
        // Setup directories
        dataDir = File(appContext.filesDir, "MCraftData")
        if (!dataDir.exists()) dataDir.mkdirs()
        
        versionsDir = File(dataDir, "Versions")
        if (!versionsDir.exists()) versionsDir.mkdirs()
        
        // Load or create data files
        loadSettingPrefs()
        loadInstalledVersions()
    }
    
    // ========== SETTINGS ==========
    
    private var _settings: SettingPrefs = SettingPrefs()
    
    fun getSettings(): SettingPrefs = _settings
    
    fun saveSettings(settings: SettingPrefs) {
        _settings = settings
        val json = gson.toJson(settings)
        prefs.edit().putString("settings", json).apply()
    }
    
    private fun loadSettingPrefs() {
        val json = prefs.getString("settings", null)
        if (json != null) {
            _settings = gson.fromJson(json, SettingPrefs::class.java)
        } else {
            // Save defaults
            saveSettings(_settings)
        }
    }
    
    // ========== INSTALLED VERSIONS ==========
    
    private var _installedVersions: InstalledVersions = InstalledVersions()
    
    fun getInstalledVersions(): InstalledVersions = _installedVersions
    
    fun saveInstalledVersions(versions: InstalledVersions) {
        _installedVersions = versions
        val json = gson.toJson(versions)
        val file = File(dataDir, "installed_versions.json")
        file.writeText(json)
    }
    
    private fun loadInstalledVersions() {
        val file = File(dataDir, "installed_versions.json")
        if (file.exists()) {
            val json = file.readText()
            _installedVersions = gson.fromJson(json, InstalledVersions::class.java)
        } else {
            // Save defaults (1.20.4 pre-installed)
            saveInstalledVersions(_installedVersions)
        }
    }
    
    // ========== VERSION MANAGEMENT ==========
    
    fun isVersionInstalled(versionId: String): Boolean {
        return _installedVersions.versions.any { it.id == versionId && it.isInstalled }
    }
    
    fun markVersionInstalled(versionId: String, versionName: String) {
        val existing = _installedVersions.versions.find { it.id == versionId }
        if (existing != null) {
            existing.isInstalled = true
        } else {
            _installedVersions.versions.add(
                InstalledVersions.InstalledVersion(versionId, versionName, true)
            )
        }
        saveInstalledVersions(_installedVersions)
    }
    
    fun markVersionUninstalled(versionId: String) {
        val existing = _installedVersions.versions.find { it.id == versionId }
        if (existing != null) {
            existing.isInstalled = false
        }
        saveInstalledVersions(_installedVersions)
    }
    
    // ========== VERSION FILES ==========
    
    fun getVersionDir(versionId: String): File {
        // Convert 1.20.4 -> V1_20_4
        val safeName = "V${versionId.replace(".", "_")}"
        val versionDir = File(versionsDir, safeName)
        if (!versionDir.exists()) versionDir.mkdirs()
        return versionDir
    }
    
    fun getAssetsDir(versionId: String): File {
        return File(getVersionDir(versionId), "Assets")
    }
    
    fun getClientJar(versionId: String): File {
        return File(getVersionDir(versionId), "client.jar")
    }
}