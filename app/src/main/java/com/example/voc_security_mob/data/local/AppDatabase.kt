
package com.example.voc_security_mob.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.voc_security_mob.data.local.dao.UserDao
import com.example.voc_security_mob.data.local.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
                    .addCallback(AppDatabaseCallback()) // On ajoute le déclencheur ici
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Le Callback qui crée l'admin par défaut
        private class AppDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val userDao = database.userDao()

                        // Création de l'utilisateur Admin par défaut
                        val admin = User(
                            name = "Admin Principal",
                            email = "admin@voc.com",
                            password = "admin", // Dans un vrai projet, on hacherait ce MDP
                            role = "ADMIN",
                            organizationName = "VOC Security Corp"
                        )
                        userDao.insertUser(admin)
                    }
                }
            }
        }
    }
}