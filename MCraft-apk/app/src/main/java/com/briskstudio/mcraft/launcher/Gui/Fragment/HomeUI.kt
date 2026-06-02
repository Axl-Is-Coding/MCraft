package com.briskstudio.mcraft.launcher.Gui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.briskstudio.mcraft.R
import com.briskstudio.mcraft.launcher.Backend.Launcher
import com.briskstudio.mcraft.launcher.Backend.VersionHandler.Data.MinecraftVersion
import com.briskstudio.mcraft.launcher.Gui.Adapter.VersionAdapter

class HomeUI : Fragment() {
    
    private lateinit var versionList: RecyclerView
    private lateinit var playButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: VersionAdapter
    
    private var currentDownloadVersion: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_ui, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        versionList = view.findViewById(R.id.version_list)
        playButton = view.findViewById(R.id.play_button)
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.GONE
        
        setupRecyclerView()
        
        playButton.setOnClickListener {
            val selectedVersion = Launcher.getSelectedVersion()
            if (selectedVersion != null && selectedVersion.isInstalled) {
                Launcher.launchGame(requireContext())
            } else if (selectedVersion != null) {
                Toast.makeText(context, "Please download ${selectedVersion.id} first", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No version selected", Toast.LENGTH_SHORT).show()
            }
        }
        
        refreshVersions()
    }
    
    private fun setupRecyclerView() {
        val versions = Launcher.getAllVersions()
        
        adapter = VersionAdapter(
            versions = versions,
            onItemClick = { version ->
                Launcher.setSelectedVersion(version)
                Toast.makeText(context, "Selected ${version.id}", Toast.LENGTH_SHORT).show()
            },
            onActionClick = { version ->
                if (version.isInstalled) {
                    Launcher.launchGame(requireContext())
                } else {
                    startDownload(version)
                }
            }
        )
        
        versionList.layoutManager = LinearLayoutManager(requireContext())
        versionList.adapter = adapter
    }
    
    private fun startDownload(version: MinecraftVersion) {
        currentDownloadVersion = version.id
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        
        Launcher.downloadVersion(
            version = version,
            onProgress = { progress ->
                requireActivity().runOnUiThread {
                    progressBar.progress = (progress * 100).toInt()
                }
            },
            onComplete = { success, message ->
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    
                    if (success) {
                        refreshVersions()
                    }
                    currentDownloadVersion = null
                }
            }
        )
    }
    
    private fun refreshVersions() {
        Launcher.refreshVersions { versions ->
            requireActivity().runOnUiThread {
                adapter.updateVersions(versions)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        if (currentDownloadVersion != null) {
            Launcher.cancelDownload()
        }
    }
}