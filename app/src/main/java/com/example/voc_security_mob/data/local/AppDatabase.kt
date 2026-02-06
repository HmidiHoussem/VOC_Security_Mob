
package com.example.voc_security_mob.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.voc_security_mob.data.local.dao.ServerDao
import com.example.voc_security_mob.data.local.dao.UserDao
import com.example.voc_security_mob.data.local.entities.Server
import com.example.voc_security_mob.data.local.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Server::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun serverDao(): ServerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "voc_database"
                )
                    .fallbackToDestructiveMigration() // Efface et recrée en cas de changement de version
                    .addCallback(AppDatabaseCallback(context)) // Recrée l'admin après l'effacement
                    .build()
                INSTANCE = instance
                instance
            }
        }

       /* pour ajouter juste user / admin par defaut !

       private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // onCreate est appelé la TOUTE PREMIÈRE FOIS (ou après un destructive migration)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.userDao())
                    }
                }
            }


        */
       private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
           override fun onCreate(db: SupportSQLiteDatabase) {
               super.onCreate(db)
               INSTANCE?.let { database ->
                   CoroutineScope(Dispatchers.IO).launch {
                       // On passe les deux DAOs ici
                       populateDatabase(database.userDao(), database.serverDao())
                   }
               }
           }
       }


        suspend fun populateDatabase(userDao: UserDao, serverDao: ServerDao) {
            //0.add user
            val admin = User(
                name = "Admin Principal",
                email = "admin@voc.com",
                password = "admin",
                role = "ADMIN",
                organizationName = "VOC Security Corp"
            )

            // 1. AJOUT DES UTILISATEURS (Admin, Managers, Analystes)

            val users = listOf(
                admin,
               // User(name = "Admin Principal",email = "admin@voc.com",password = "admin",role = "ADMIN",organizationName = "VOC Security Corp"),
                User(name = "Admin VOC", email = "admin@voc.tn", password = "admin", role = "ADMIN", organizationName = "VOC Security"),
                User(name = "Amine Ben Salem", email = "amine@tt.tn", password = "123", role = "MANAGER", organizationName = "Tunisie Telecom"),
                User(name = "Sarra Mansour", email = "sarra@biat.tn", password = "123", role = "MANAGER", organizationName = "BIAT"),
                User(name = "Youssef Trabelsi", email = "youssef@steg.tn", password = "123", role = "ANALYSTE", organizationName = "STEG")
            )
            users.forEach { userDao.insertUser(it) }

            // 2. AJOUT DES SERVEURS (Assets)
            val servers = listOf(
                // Serveurs Tunisie Telecom
                Server(serverName = "TT-Main-Router", ipAddress = "192.168.1.1", os = "Cisco IOS", criticality = "Haute", organizationOwner = "Tunisie Telecom", securityScore = 85),
                Server(serverName = "TT-Billing-DB", ipAddress = "10.0.0.5", os = "Linux Ubuntu", criticality = "Haute", organizationOwner = "Tunisie Telecom", securityScore = 42),

                // Serveurs BIAT
                Server(serverName = "BIAT-ATM-Gateway", ipAddress = "172.16.5.10", os = "Windows Server", criticality = "Haute", organizationOwner = "BIAT", securityScore = 95),
                Server(serverName = "BIAT-Web-Portal", ipAddress = "172.16.5.11", os = "Linux Debian", criticality = "Moyenne", organizationOwner = "BIAT", securityScore = 65),

                // Serveurs STEG
                Server(serverName = "STEG-Control-Panel", ipAddress = "10.50.1.1", os = "CentOS", criticality = "Haute", organizationOwner = "STEG", securityScore = 30),
                Server(serverName = "STEG-Public-WiFi", ipAddress = "10.50.1.20", os = "MikroTik", criticality = "Basse", organizationOwner = "STEG", securityScore = 88)
            )
            servers.forEach { serverDao.insertServer(it) }
        }

        /*

            suspend fun populateDatabase(userDao: UserDao, serverDao: ServerDao) {
                           val admin = User(
                    name = "Admin Principal",
                    email = "admin@voc.com",
                    password = "admin",
                    role = "ADMIN",
                    organizationName = "VOC Security Corp"
                )
                userDao.insertUser(admin)
               println("Base de données initialisée avec l'Admin par défaut.")
            }
        }*/
    }


}