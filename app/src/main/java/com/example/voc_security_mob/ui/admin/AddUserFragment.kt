package com.example.voc_security_mob.ui.admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.local.entities.User
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityAddUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddUserFragment : Fragment(R.layout.activity_add_user) {
    private var _binding: ActivityAddUserBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityAddUserBinding.bind(view)

        val db = AppDatabase.getDatabase(requireContext())
        val repo = UserRepository(db.userDao())

        // Dans onViewCreated du AddUserFragment
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val currentUserRole = sharedPref.getString("USER_ROLE", "")
        val currentUserOrg = sharedPref.getString("USER_ORG", "")

// 1. Forcer l'organisation pour le Manager
        if (currentUserRole == "MANAGER") {
            binding.etNewOrganization.setText(currentUserOrg)
            binding.etNewOrganization.isEnabled = false // Il ne peut pas créer pour une autre boîte
        }

// 2. Filtrer les rôles disponibles dans le Spinner
        val roles = if (currentUserRole == "ADMIN") {
            arrayOf("ADMIN", "MANAGER", "ANALYSTE")
        } else {
            arrayOf("MANAGER", "ANALYSTE") // Le manager ne peut pas créer d'Admin
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        binding.spinnerRole.adapter = adapter



        binding.btnSaveUser.setOnClickListener {

            val user = User(
                name = binding.etNewName.text.toString().trim(),
                email = binding.etNewEmail.text.toString().trim(),
                password = binding.etNewPassword.text.toString().trim(),
                role = binding.spinnerRole.selectedItem.toString(),
                organizationName = binding.etNewOrganization.text.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                repo.insert(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Utilisateur ajouté !", Toast.LENGTH_SHORT).show()
                    // Retour automatique à la liste après ajout
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container, UserListFragment()).commit()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}