package com.example.voc_security_mob.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.data.local.entities.User
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    // Fonction pour vérifier les identifiants
    fun login(email: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            onResult(user)
        }
    }
}