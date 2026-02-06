package com.example.voc_security_mob.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class Server(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverName: String,
    val ipAddress: String,
    val os: String,            // ex: Linux, Windows, Cisco
    val criticality: String,   // ex: Haute, Moyenne, Basse
    val organizationOwner: String, // Pour le filtrage par société
    val lastScanDate: String = "Jamais",
    val securityScore: Int = 100 // Score de sécurité (0-100)
)
