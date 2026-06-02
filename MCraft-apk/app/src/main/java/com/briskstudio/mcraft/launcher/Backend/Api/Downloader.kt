package com.briskstudio.mcraft.launcher.Backend.Api

import android.content.Context
import com.briskstudio.mcraft.UserData.Userdata
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.zip.ZipFile

object Downloader {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private const val VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
    private const val ASSETS_BASE_URL = "https://resources.download.minecraft.net/"
    
    data class VersionInfo(
        val id: String,
        val url: String,
        val releaseTime: String,
        val type: String
    )
    
    data class AssetIndex(
        val id: String,
        val url: String,
        val totalSize: Long
    )
    
    // ========== FETCH VERSION LIST ==========
    
    suspend fun fetchVersionList(): List<VersionInfo> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(URL(VERSION_MANIFEST_URL).readText())
            val versionsArray = json.getJSONArray("versions")
            val versions = mutableListOf<VersionInfo>()
            
            for (i in 0 until versionsArray.length()) {
                val obj = versionsArray.getJSONObject(i)
                versions.add(
                    VersionInfo(
                        id = obj.getString("id"),
                        url = obj.getString("url"),
                        releaseTime = obj.getString("releaseTime"),
                        type = obj.getString("type")
                    )
                )
            }
            versions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    // ========== DOWNLOAD CLIENT.JAR (Assets only) ==========
    
    fun downloadClientJar(
        version: String,
        onProgress: (Float) -> Unit = {},
        onComplete: (Boolean) -> Unit = {}
    ) {
        scope.launch {
            try {
                // 1. Get version info from Mojang
                val versionUrl = getVersionJsonUrl(version)
                if (versionUrl == null) {
                    withContext(Dispatchers.Main) { onComplete(false) }
                    return@launch
                }
                
                onProgress(0.05f)
                
                // 2. Parse version.json to get client.jar URL
                val versionJson = JSONObject(URL(versionUrl).readText())
                val downloads = versionJson.getJSONObject("downloads")
                val clientObj = downloads.getJSONObject("client")
                val clientUrl = clientObj.getString("url")
                val clientSize = clientObj.getLong("size")
                
                onProgress(0.1f)
                
                // 3. Download client.jar
                val clientFile = Userdata.getClientJar(version)
                if (!clientFile.exists() || clientFile.length() != clientSize) {
                    downloadFile(clientUrl, clientFile) { progress ->
                        onProgress(0.1f + (progress * 0.4f))
                    }
                }
                
                onProgress(0.5f)
                
                // 4. Extract assets from client.jar
                extractAssetsFromJar(version, clientFile) { progress ->
                    onProgress(0.5f + (progress * 0.4f))
                }
                
                // 5. Download asset index
                downloadAssetIndex(version, versionJson) { progress ->
                    onProgress(0.9f + (progress * 0.1f))
                }
                
                onProgress(1f)
                withContext(Dispatchers.Main) { onComplete(true) }
                
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onComplete(false) }
            }
        }
    }
    
    // ========== GET VERSION JSON URL ==========
    
    private suspend fun getVersionJsonUrl(version: String): String? = withContext(Dispatchers.IO) {
        try {
            val manifest = JSONObject(URL(VERSION_MANIFEST_URL).readText())
            val versionsArray = manifest.getJSONArray("versions")
            
            for (i in 0 until versionsArray.length()) {
                val obj = versionsArray.getJSONObject(i)
                if (obj.getString("id") == version) {
                    return@withContext obj.getString("url")
                }
            }
            null
        } catch (e: Exception) {
            // Fallback for known versions
            when (version) {
                "1.20.4" -> "https://piston-meta.mojang.com/v1/packages/8dd1a28015f51b1803213895158f8aa1b5d1d15b/1.20.4.json"
                "1.18.2" -> "https://piston-meta.mojang.com/v1/packages/3cf24a8694aca626f2371ea6b4767f57a771616f/1.18.2.json"
                else -> null
            }
        }
    }
    
    // ========== EXTRACT ASSETS FROM CLIENT.JAR ==========
    
    private suspend fun extractAssetsFromJar(
        version: String,
        jarFile: File,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val assetsDir = Userdata.getAssetsDir(version)
            if (!assetsDir.exists()) assetsDir.mkdirs()
            
            ZipFile(jarFile).use { zip ->
                val entries = zip.entries().toList()
                val assetEntries = entries.filter { it.name.startsWith("assets/") }
                var processed = 0
                
                for (entry in assetEntries) {
                    val outFile = File(assetsDir, entry.name.replace("assets/", ""))
                    if (!entry.isDirectory) {
                        outFile.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            outFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                    processed++
                    onProgress(processed.toFloat() / assetEntries.size)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // ========== DOWNLOAD ASSET INDEX ==========
    
    private suspend fun downloadAssetIndex(
        version: String,
        versionJson: JSONObject,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val assetIndex = versionJson.getJSONObject("assetIndex")
            val indexUrl = assetIndex.getString("url")
            val indexId = assetIndex.getString("id")
            
            val indexFile = File(Userdata.getAssetsDir(version), "indexes/$indexId.json")
            indexFile.parentFile?.mkdirs()
            
            // Download asset index
            downloadFile(indexUrl, indexFile) { progress ->
                onProgress(progress)
            }
            
            // Parse asset index and download individual assets
            val indexJson = JSONObject(indexFile.readText())
            val objects = indexJson.getJSONObject("objects")
            val totalObjects = objects.length()
            var processed = 0
            
            val keys = objects.keys().asSequence().toList()
            for (key in keys) {
                val assetObj = objects.getJSONObject(key)
                val hash = assetObj.getString("hash")
                val hashPrefix = hash.take(2)
                
                val assetUrl = "$ASSETS_BASE_URL$hashPrefix/$hash"
                val assetFile = File(Userdata.getAssetsDir(version), "objects/$hashPrefix/$hash")
                
                if (!assetFile.exists()) {
                    assetFile.parentFile?.mkdirs()
                    downloadFile(assetUrl, assetFile) { }
                }
                
                processed++
                onProgress(processed.toFloat() / totalObjects)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // ========== FILE DOWNLOAD UTILITY ==========
    
    private suspend fun downloadFile(
        url: String,
        dest: File,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            val contentLength = connection.contentLength
            var totalBytes = 0L
            
            connection.getInputStream().use { input ->
                dest.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead
                        if (contentLength > 0) {
                            onProgress(totalBytes.toFloat() / contentLength)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    
    // ========== CHECK IF VERSION IS FULLY INSTALLED ==========
    
    suspend fun isVersionFullyInstalled(version: String): Boolean = withContext(Dispatchers.IO) {
        val clientJar = Userdata.getClientJar(version)
        val assetsDir = Userdata.getAssetsDir(version)
        
        clientJar.exists() && assetsDir.exists() && assetsDir.listFiles()?.isNotEmpty() == true
    }
    
    // ========== DELETE VERSION FILES ==========
    
    fun deleteVersion(version: String, onComplete: (Boolean) -> Unit = {}) {
        scope.launch {
            try {
                val versionDir = Userdata.getVersionDir(version)
                versionDir.deleteRecursively()
                withContext(Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onComplete(false) }
            }
        }
    }
}