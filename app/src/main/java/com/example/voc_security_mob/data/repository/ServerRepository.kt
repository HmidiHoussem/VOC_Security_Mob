package com.example.voc_security_mob.data.repository

import com.example.voc_security_mob.data.local.dao.ServerDao
import com.example.voc_security_mob.data.local.entities.Server
import kotlinx.coroutines.flow.Flow

class ServerRepository(private val serverDao: ServerDao) {

    // Récupérer tous les serveurs (pour l'Admin)
    val allServers: Flow<List<Server>> = serverDao.getAllServers()

    // Récupérer les serveurs d'une organisation spécifique (pour le Manager/Analyste)
    fun getServersByOrg(orgName: String): Flow<List<Server>> {
        return serverDao.getServersByOrganization(orgName)
    }

    // Ajouter un serveur
    suspend fun insert(server: Server) {
        serverDao.insertServer(server)
    }

    // Mettre à jour (utile pour le score de sécurité après un scan)
    suspend fun update(server: Server) {
        serverDao.updateServer(server)
    }

    // Supprimer un serveur
    suspend fun delete(server: Server) {
        serverDao.deleteServer(server)
    }

    // Compteur pour le Dashboard
    val serverCount: Flow<Int> = serverDao.getServerCount()
}
