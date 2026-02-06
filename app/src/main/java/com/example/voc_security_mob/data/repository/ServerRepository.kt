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

    suspend fun getServerById(id: Int): Server? {
        return serverDao.getServerById(id)
    }

    // Score Global (Admin)
    val globalScore: Flow<Double?> = serverDao.getGlobalScore()

    // Score Global (Manager - par organisation)
    fun getOrgGlobalScore(org: String): Flow<Double?> = serverDao.getOrgGlobalScore(org)

    // Alertes Critiques (On pourrait aussi filtrer par Org si besoin)
    val criticalAlertsCount: Flow<Int> = serverDao.getCriticalAlertsCount()

    // Nombre total de serveurs (déjà présent mais utile pour la carte Scan)
    val totalServersCount: Flow<Int> = serverDao.getServerCount()


    // role autre

    fun getServerCountByOrg(orgName: String): Flow<Int> = serverDao.getServerCountByOrg(orgName)

    fun getCriticalAlertsCountByOrg(orgName: String): Flow<Int> = serverDao.getCriticalAlertsCountByOrg(orgName)



    //scan rapide
    // Récupérer la liste fixe pour le scan (sans Flow)
    suspend fun getServersListSync(role: String, org: String): List<Server> {
        return if (role == "ADMIN") {
            serverDao.getAllServersSync()
        } else {
            serverDao.getServersByOrgSync(org)
        }
    }


}
