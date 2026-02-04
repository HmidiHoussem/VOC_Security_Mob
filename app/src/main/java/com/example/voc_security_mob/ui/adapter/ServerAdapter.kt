package com.example.voc_security_mob.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voc_security_mob.data.local.entities.Server
import com.example.voc_security_mob.R
import androidx.core.graphics.toColorInt

class ServerAdapter(private var servers: List<Server>, private val onServerClick: (Server) -> Unit) :
    RecyclerView.Adapter<ServerAdapter.ServerViewHolder>() {

    class ServerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvServerName)
        val ip: TextView = view.findViewById(R.id.tvServerIp)
        val score: TextView = view.findViewById(R.id.tvSecurityScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_server, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = servers[position]
        holder.name.text = server.serverName
        holder.ip.text = server.ipAddress
        holder.score.text = "${server.securityScore}%"

        // Couleur selon le score
        //if(server.securityScore < 75) holder.score.setTextColor("#FBC02D".toColorInt())
        //if(server.securityScore < 50) holder.score.setTextColor(Color.RED)
//pour definir les couleur d'apré le score !
// 1. On détermine l'ID de la ressource
        val colorResId = when {
            server.securityScore >= 75 -> R.color.score_high   // Vert
            server.securityScore >= 50 -> R.color.score_medium // Orange
            else -> R.color.score_low                         // Rouge
        }

// 2. On convertit l'ID en véritable couleur
        val actualColor = androidx.core.content.ContextCompat.getColor(holder.itemView.context, colorResId)

// 3. On applique la vraie couleur au texte
        holder.score.setTextColor(actualColor)


        holder.itemView.setOnClickListener { onServerClick(server) }
    }

    override fun getItemCount() = servers.size

    fun updateData(newServers: List<Server>) {
        this.servers = newServers
        notifyDataSetChanged()
    }
}
