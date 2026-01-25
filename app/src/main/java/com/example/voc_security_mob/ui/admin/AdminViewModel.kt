package com.example.voc_security_mob.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voc_security_mob.data.local.entities.User
import com.example.voc_security_mob.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: UserRepository) : ViewModel() {

    fun addUser(user: User) {
        viewModelScope.launch {
            repository.insert(user)
        }
    }
}