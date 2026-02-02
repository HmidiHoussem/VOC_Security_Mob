package com.example.voc_security_mob.ui.analyst

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.local.entities.Server
import com.example.voc_security_mob.data.repository.ServerRepository
import com.example.voc_security_mob.databinding.FragmentScanBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScanFragment : Fragment(R.layout.fragment_scan) {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    // On passera l'ID du serveur via les arguments
    private var serverId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentScanBinding.bind(view)

        serverId = arguments?.getInt("SERVER_ID") ?: -1

        val db = AppDatabase.getDatabase(requireContext())
        val repository = ServerRepository(db.serverDao())

        // 1. Récupérer les infos du serveur
        viewLifecycleOwner.lifecycleScope.launch {
            // Ici on récupère le serveur par son ID (pense à ajouter cette fonction dans ton repo/dao)
            // Pour l'exemple, on simule une recherche
            startScanProcess(repository)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private suspend fun startScanProcess(repository: ServerRepository) {
        // Simulation de l'analyse cyber (Mock)
        delay(3000) // 3 secondes de suspense

        val randomScore = (20..100).random()
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // Mise à jour visuelle
        binding.progressBarScan.visibility = View.GONE
        binding.tvStatus.text = "Scan Terminé !\nScore de sécurité : $randomScore%"
        binding.btnBack.visibility = View.VISIBLE

        // 2. Sauvegarde en base de données
        // Note: Tu devras récupérer l'objet Server complet via son ID avant de faire ça
        /*
        val server = repository.getServerById(serverId)
        server?.let {
            val updatedServer = it.copy(securityScore = randomScore, lastScanDate = date)
            repository.update(updatedServer)
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}