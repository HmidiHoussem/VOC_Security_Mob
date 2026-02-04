package com.example.voc_security_mob.ui.common

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.voc_security_mob.R
import com.example.voc_security_mob.databinding.FragmentGeneralProfileBinding
import com.example.voc_security_mob.ui.auth.LoginActivity



class GeneralProfileFragment : Fragment(R.layout.fragment_general_profile) {

    private var _binding: FragmentGeneralProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGeneralProfileBinding.bind(view)

        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Récupération via SharedPreferences
        val name = sharedPref.getString("USER_NAME", "Utilisateur")
        val role = sharedPref.getString("USER_ROLE", "ANALYSTE")
        val email = sharedPref.getString("USER_EMAIL", "")
        val org = sharedPref.getString("USER_ORG", "")
        val id = sharedPref.getString("USER_ID", "0")

        binding.apply {
            tvProfileName.text = name
            tvProfileEmail.text = email
            tvProfileOrg.text = org
            tvProfileID.text = "UID: $id"
            tvRoleBadge.text = role

            // Badge de couleur selon le rôle
            if (role == "ADMIN") {
                tvRoleBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E91E63")) // Rose/Rouge
            } else {
                tvRoleBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2196F3")) // Bleu
            }

            // Navigation vers la modification

            btnEditProfile.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EditProfileFragment())
                    .addToBackStack(null)
                    .commit()
            }

            btnLogout.setOnClickListener {
                logout()
            }
        }
    }

    private fun logout() {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }
}