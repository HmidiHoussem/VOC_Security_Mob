package com.example.voc_security_mob.ui.analyst

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.ServerRepository
import com.example.voc_security_mob.databinding.FragmentServerListBinding
import com.example.voc_security_mob.ui.adapter.ServerAdapter
import kotlinx.coroutines.launch

class ServerListFragment : Fragment(R.layout.fragment_server_list) {

    private var _binding: FragmentServerListBinding? = null
    private val binding get() = _binding!!

    // Déclaration de l'adapter en propriété de classe pour être accessible partout
    private lateinit var adapter: ServerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServerListBinding.bind(view)

        val db = AppDatabase.getDatabase(requireContext())
        val repository = ServerRepository(db.serverDao())

        // Initialisation de l'instance de l'adapter
        adapter = ServerAdapter(emptyList()) { server ->
            val scanFragment = ScanFragment()
            val args = Bundle()
            args.putInt("SERVER_ID", server.id)
            scanFragment.arguments = args

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, scanFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvServers.adapter = adapter

        // Récupération des données selon le rôle
        viewLifecycleOwner.lifecycleScope.launch {
            val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val role = sharedPref.getString("USER_ROLE", "ANALYSTE") ?: "ANALYSTE"
            val org = sharedPref.getString("USER_ORG", "") ?: ""

            if (role == "ADMIN") {
                repository.allServers.collect { adapter.updateData(it) }
            } else {
                repository.getServersByOrg(org).collect { adapter.updateData(it) }
            }
        }

        binding.fabAddServer.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddServerFragment())
                .addToBackStack(null)
                .commit()
        }

        // Activation du Swipe
        setupSwipeToDeleteServer(repository)
    }

    private fun setupSwipeToDeleteServer(repository: ServerRepository) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // On utilise l'instance 'adapter' pour récupérer le serveur
                val server = adapter.getServerAt(position)

                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Suppression")
                    .setMessage("Supprimer définitivement le serveur ${server.serverName} ?")
                    .setPositiveButton("Oui") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            repository.delete(server)
                            Toast.makeText(context, "Serveur supprimé", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Annuler") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .setCancelable(false) // L'utilisateur doit choisir
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvServers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}