package com.example.voc_security_mob.ui.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.databinding.FragmentEditProfileBinding
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", "0")?.toInt() ?: 0

        // Pré-remplir les champs avec les données actuelles
        binding.etEditName.setText(sharedPref.getString("USER_NAME", ""))
        binding.etEditEmail.setText(sharedPref.getString("USER_EMAIL", ""))

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString()
            val newEmail = binding.etEditEmail.text.toString()

            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(requireContext())

                    // 1. Mise à jour dans la Base de Données Room
                    db.userDao().updateUserProfile(userId, newName, newEmail)

                    // 2. Mise à jour des SharedPreferences (CRUCIAL)
                    with(sharedPref.edit()) {
                        putString("USER_NAME", newName)
                        putString("USER_EMAIL", newEmail)
                        apply()
                    }

                    Toast.makeText(context, "Profil mis à jour !", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack() // Retour au profil
                }
            }
        }
    }
}