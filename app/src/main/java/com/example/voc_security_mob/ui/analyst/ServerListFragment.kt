package com.example.voc_security_mob.ui.analyst

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.voc_security_mob.R


import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.ServerRepository
import com.example.voc_security_mob.databinding.FragmentServerListBinding
import com.example.voc_security_mob.ui.adapter.ServerAdapter
import kotlinx.coroutines.launch

class ServerListFragment : Fragment(R.layout.fragment_server_list) {

    private var _binding: FragmentServerListBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServerListBinding.bind(view)

        // 1. Initialisation du Repository
        val db = AppDatabase.getDatabase(requireContext())
        val repository = ServerRepository(db.serverDao())

        // 2. Initialisation de l'Adapter avec la logique de clic vers le SCAN
        // On l'appelle 'it' ou 'serverList' dans le collect plus bas
        val adapter = ServerAdapter(emptyList()) { server ->
            // ACTION : Aller vers le ScanFragment quand on clique
            val scanFragment = ScanFragment()

            // On passe l'ID du serveur au fragment de scan
            val args = Bundle()
            args.putInt("SERVER_ID", server.id)
            scanFragment.arguments = args

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, scanFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvServers.adapter = adapter

        // 3. Récupération des données (C'est ici que 'list' est défini)
        viewLifecycleOwner.lifecycleScope.launch {
            // On récupère le rôle et l'organisation pour filtrer
            val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val role = sharedPref.getString("USER_ROLE", "ANALYSTE") ?: "ANALYSTE"
            val org = sharedPref.getString("USER_ORG", "") ?: ""

            if (role == "ADMIN") {
                // L'admin voit TOUS les serveurs
                repository.allServers.collect { serveursRecuperes ->
                    adapter.updateData(serveursRecuperes)
                }
            } else {
                // Le Manager/Analyste ne voit que les serveurs de son organisation
                repository.getServersByOrg(org).collect { serveursRecuperes ->
                    adapter.updateData(serveursRecuperes)
                }
            }
        }

        // 4. Bouton pour ajouter un serveur
        binding.fabAddServer.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddServerFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


