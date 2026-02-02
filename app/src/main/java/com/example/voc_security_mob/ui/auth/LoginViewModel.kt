package com.example.voc_security_mob.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.data.local.entities.User
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            onResult(user)
        }
    }
}