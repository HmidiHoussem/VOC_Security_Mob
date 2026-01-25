package com.example.voc_security_mob.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.voc_security_mob.data.local.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Inscription / Ajout d'utilisateur
    @Insert
    suspend fun insertUser(user: User)

    // Pour le Login : cherche l'utilisateur par son email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Liste de tous les utilisateurs (pour l'Admin)
    @Query("SELECT * FROM users")
    fun getAllUsers(): kotlinx.coroutines.flow.Flow<List<User>>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)


    // pour homepage calcul genereal

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT organizationName) FROM users")
    fun getOrganizationCount(): Flow<Int>
}