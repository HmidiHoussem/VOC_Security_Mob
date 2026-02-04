package com.example.voc_security_mob.data.local.dao

import androidx.room.*
import com.example.voc_security_mob.data.local.entities.Server
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: Server)

    @Query("SELECT * FROM servers ORDER BY id DESC")
    fun getAllServers(): Flow<List<Server>>

    @Query("SELECT * FROM servers WHERE organizationOwner = :orgName")
    fun getServersByOrganization(orgName: String): Flow<List<Server>>

    @Update
    suspend fun updateServer(server: Server)

    @Delete
    suspend fun deleteServer(server: Server)

    @Query("SELECT COUNT(*) FROM servers")
    fun getServerCount(): Flow<Int>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerById(id: Int): Server?
}