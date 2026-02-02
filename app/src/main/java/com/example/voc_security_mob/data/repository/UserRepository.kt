package com.example.voc_security_mob.data.repository

import com.example.voc_security_mob.data.local.dao.UserDao
import com.example.voc_security_mob.data.local.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    // Récupérer tous les utilisateurs pour l'Admin
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    // Fonction pour l'inscription ou l'ajout par l'Admin
    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }

    // Fonction pour vérifier le login
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }


    val userCount: Flow<Int> = userDao.getUserCount()
    val organizationCount: Flow<Int> = userDao.getOrganizationCount()


    // getUsersByOrganization
    fun getUsersByOrganization(orgName: String): Flow<List<User>> {
        return userDao.getUsersByOrganization(orgName)
    }
    //login
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }
}