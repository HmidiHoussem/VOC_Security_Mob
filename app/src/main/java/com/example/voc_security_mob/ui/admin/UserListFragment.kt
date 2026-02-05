package com.example.voc_security_mob.ui.admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.FragmentUserListBinding
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

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
        setupSwipeToDelete(repository)

    }


    private fun setupSwipeToDelete(repository: UserRepository) {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getString("USER_ID", "")?.toInt() ?: -1

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val userToDelete = adapter.getUserAt(position) // Vérifie que cette fonction est dans ton UserAdapter

                if (userToDelete.id == currentUserId) {
                    Toast.makeText(context, "Impossible de supprimer votre propre compte !", Toast.LENGTH_SHORT).show()
                    adapter.notifyItemChanged(position)
                    return
                }

                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Suppression")
                    .setMessage("Supprimer l'utilisateur ${userToDelete.name} ?")
                    .setPositiveButton("Supprimer") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            repository.deleteUser(userToDelete) // Assure-toi que deleteUser existe dans ton Repo
                        }
                    }
                    .setNegativeButton("Annuler") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvUsers)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}