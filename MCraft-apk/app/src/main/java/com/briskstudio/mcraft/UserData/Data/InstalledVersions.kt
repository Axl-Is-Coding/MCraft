package com.briskstudio.mcraft.UserData.Data

data class InstalledVersions(
    val versions: MutableList<InstalledVersion> = mutableListOf(
        InstalledVersion("1.20.4", "Minecraft 1.20.4", true)  // Pre-installed
    )
) {
    data class InstalledVersion(
        val id: String,
        val name: String,
        var isInstalled: Boolean
    )
}