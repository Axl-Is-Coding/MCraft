package com.briskstudio.mcraft.launcher.Backend.SettingsHandler

import com.briskstudio.mcraft.UserData.Userdata
import com.briskstudio.mcraft.UserData.Data.SettingPrefs

object Settings {
    
    fun getSettings(): SettingPrefs {
        return Userdata.getSettings()
    }
    
    fun saveSettings(settings: SettingPrefs) {
        Userdata.saveSettings(settings)
    }
    
    fun updateRenderDistance(distance: Int) {
        val settings = getSettings()
        settings.renderDistance = distance
        saveSettings(settings)
    }
    
    fun updateVolume(volume: Int) {
        val settings = getSettings()
        settings.musicVolume = volume
        settings.soundVolume = volume
        saveSettings(settings)
    }
    
    fun toggleTouchControls(enabled: Boolean) {
        val settings = getSettings()
        settings.touchControls = enabled
        saveSettings(settings)
    }
    
    fun toggleShowFps(enabled: Boolean) {
        val settings = getSettings()
        settings.showFps = enabled
        saveSettings(settings)
    }
    
    fun getRenderDistance(): Int = getSettings().renderDistance
    fun getMusicVolume(): Int = getSettings().musicVolume
    fun isTouchControlsEnabled(): Boolean = getSettings().touchControls
    fun isShowFpsEnabled(): Boolean = getSettings().showFps
}