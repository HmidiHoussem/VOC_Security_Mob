
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

@Database(entities = [User::class, Server::class], version = 2, exportSchema = false)
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

            suspend fun populateDatabase(userDao: UserDao) {
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
        }
    }
}