package com.example.voc_security_mob.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityUserListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        adapter = UserAdapter(emptyList())
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter

        // Récupérer les données
        val db = AppDatabase.getDatabase(this)
        val repository = UserRepository(db.userDao())

        // Observer les changements dans la DB (Flow)
        CoroutineScope(Dispatchers.IO).launch {
            repository.allUsers.collect { userList ->
                withContext(Dispatchers.Main) {
                    adapter.updateData(userList)
                }
            }
        }
    }
}