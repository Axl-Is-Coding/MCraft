package com.briskstudio.mcraft.launcher.Backend.VersionHandler.Data

data class MinecraftVersion(
    val id: String,
    val name: String,
    val isInstalled: Boolean,
    val isRecommended: Boolean = false,
    val isLatest: Boolean = false,
    val isLegacy: Boolean = false
) {
    fun getBadge(): String? = when {
        isLatest -> "LATEST"
        isRecommended -> "RECOMMENDED"
        isLegacy -> "LEGACY"
        else -> null
    }
    
    fun getBadgeColor(): String = when {
        isLatest -> "#44AAFF"
        isRecommended -> "#44AA44"
        isLegacy -> "#FFAA44"
        else -> ""
    }
    
    fun getButtonText(): String = if (isInstalled) "PLAY" else "GET"
    
    fun getButtonColor(): String = if (isInstalled) "#44AA44" else "#2D2D2D"
}