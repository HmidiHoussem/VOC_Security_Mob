package com.example.voc_security_mob.ui.admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.FragmentUserListBinding
import kotlinx.coroutines.launch

class UserListFragment : Fragment(R.layout.fragment_user_list) {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserListBinding.bind(view)

        // 1. Configuration du RecyclerView
        adapter = UserAdapter(emptyList())
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        // 2. Initialisation Database et Repository
        val db = AppDatabase.getDatabase(requireContext())
        val repository = UserRepository(db.userDao())

        // 3. Gestion de la Navigation vers AddUserFragment
        binding.fabAddUser.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddUserFragment()) // Remplacer par l'ID de ton container principal
                .addToBackStack(null) // Permet de revenir en arrière avec le bouton "Retour"
                .commit()
        }

        // 4. Récupération de la session utilisateur
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "")
        val org = sharedPref.getString("USER_ORG", "") ?: ""

        // 5. Chargement dynamique des données
        viewLifecycleOwner.lifecycleScope.launch {
            if (role == "ADMIN") {
                // L'admin voit tout le monde
                repository.allUsers.collect { list ->
                    adapter.updateData(list)
                }
            } else {
                // Le Manager (ou autre) ne voit que les membres de son organisation
                repository.getUsersByOrganization(org).collect { list ->
                    adapter.updateData(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}