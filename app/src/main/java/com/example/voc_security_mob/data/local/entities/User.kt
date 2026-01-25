package com.example.voc_security_mob.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // Le nom du tableau dans la DB
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val email: String, // Pour le login
    val password: String,

    val role: String, // "ADMIN", "ANALYSTE", "MANAGER"
    val organizationName: String // Nom de la société / organisme
)