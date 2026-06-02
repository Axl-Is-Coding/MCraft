package com.briskstudio.mcraft.launcher.Gui.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.briskstudio.mcraft.R
import com.briskstudio.mcraft.launcher.Backend.VersionHandler.Data.MinecraftVersion

class VersionAdapter(
    private var versions: List<MinecraftVersion>,
    private val onItemClick: (MinecraftVersion) -> Unit,
    private val onActionClick: (MinecraftVersion) -> Unit
) : RecyclerView.Adapter<VersionAdapter.ViewHolder>() {
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVersion: TextView = itemView.findViewById(R.id.tv_version)
        val tvBadge: TextView = itemView.findViewById(R.id.tv_badge)
        val btnAction: Button = itemView.findViewById(R.id.btn_action)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_version, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = versions[position]
        
        holder.tvVersion.text = item.name
        
        // Set badge
        val badge = item.getBadge()
        if (badge != null) {
            holder.tvBadge.text = badge
            holder.tvBadge.visibility = View.VISIBLE
            holder.tvBadge.setTextColor(Color.parseColor(item.getBadgeColor()))
        } else {
            holder.tvBadge.visibility = View.GONE
        }
        
        // Set button
        holder.btnAction.text = item.getButtonText()
        holder.btnAction.backgroundTintList = android.content.res.ColorStateList.valueOf(
            Color.parseColor(item.getButtonColor())
        )
        
        // Click listeners
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.btnAction.setOnClickListener { onActionClick(item) }
    }
    
    override fun getItemCount() = versions.size
    
    fun updateVersions(newVersions: List<MinecraftVersion>) {
        versions = newVersions
        notifyDataSetChanged()
    }
}