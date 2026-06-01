package de.bixilon.minosoft.gui.qmcl.main.play.server

import androidx.recyclerview.widget.RecyclerView

class ServerListController : RecyclerView.Adapter<ServerListController.ViewHolder>() {
    
    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView)
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        TODO("Implement server list UI for future multiplayer")
    }
    
    override fun getItemCount(): Int = 0
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind server data
    }
}
