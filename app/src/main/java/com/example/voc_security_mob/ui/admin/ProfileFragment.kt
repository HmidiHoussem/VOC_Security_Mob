package com.example.voc_security_mob.ui.admin



import android.view.LayoutInflater

import android.view.ViewGroup



import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.voc_security_mob.R
import com.example.voc_security_mob.databinding.FragmentProfileBinding
import com.example.voc_security_mob.ui.auth.LoginActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        // Affichage des informations statiques pour l'instant
        binding.tvProfileName.text = "Administrateur VOC"
        binding.tvProfileEmail.text = "admin@voc.com"
        binding.tvProfileRole.text = "Rôle : Super Admin"

        // Gestion du Logout
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Déconnexion")
            .setMessage("Voulez-vous vraiment quitter la session ?")
            .setPositiveButton("Oui") { _, _ ->
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Non", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}