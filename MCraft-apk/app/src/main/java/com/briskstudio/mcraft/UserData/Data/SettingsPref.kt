package com.briskstudio.mcraft.UserData.Data

data class SettingPrefs(
    var renderDistance: Int = 8,
    var musicVolume: Int = 50,
    var soundVolume: Int = 50,
    var showFps: Boolean = true,
    var touchControls: Boolean = true,
    var autoSave: Boolean = true,
    var lastPlayedVersion: String? = "1.20.4",
    var theme: String = "dark"
)