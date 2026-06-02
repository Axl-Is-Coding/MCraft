package com.briskstudio.mcraft.launcher

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.briskstudio.mcraft.R
import com.briskstudio.mcraft.game.GameActivity

class LauncherActivity : AppCompatActivity() {
    
    private lateinit var versionList: RecyclerView
    private lateinit var playButton: Button
    private lateinit var versionManager: VersionManager
    private lateinit var adapter: VersionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        
        // Initialize views
        versionList = findViewById(R.id.version_list)
        playButton = findViewById(R.id.play_button)
        
        // Setup version manager
        versionManager = VersionManager(this)
        versionManager.loadInstalledVersions()
        
        // Setup recycler view
        adapter = VersionAdapter(versionManager.getVersions()) { version ->
            versionManager.selectVersion(version)
        }
        versionList.layoutManager = LinearLayoutManager(this)
        versionList.adapter = adapter
        
        // Setup play button
        playButton.setOnClickListener {
            val selected = versionManager.getSelectedVersion()
            if (selected != null) {
                launchGame(selected)
            }
        }
    }
    
    private fun launchGame(version: MinecraftVersion) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("VERSION_ID", version.id)
            putExtra("VERSION_NAME", version.name)
        }
        startActivity(intent)
    }
}