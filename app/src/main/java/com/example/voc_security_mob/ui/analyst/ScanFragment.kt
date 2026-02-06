package com.example.voc_security_mob.ui.analyst

import android.os.Bundle
import android.view.View
import android.widget.Toast
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

/*
class ScanFragment : Fragment(R.layout.fragment_scan) {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    // On passera l'ID du serveur via les arguments
    private var serverId: Int = -1


    ///////////////////////////////////////////////////////////////
    // Data class pour structurer un problème trouvé
    data class Vulnerability(val title: String, val severity: String)

    private fun generateVulnerabilities(score: Int): List<Vulnerability> {
        val list = mutableListOf<Vulnerability>()
        if (score < 90) list.add(Vulnerability("Ports non sécurisés ouverts (SSH/Telnet)", "Élevée"))
        if (score < 70) list.add(Vulnerability("Certificat SSL expiré ou autosigné", "Moyenne"))
        if (score < 50) list.add(Vulnerability("OS obsolète (besoin de mise à jour)", "Critique"))
        if (score < 30) list.add(Vulnerability("Mots de passe par défaut détectés", "Critique"))

        if (list.isEmpty()) list.add(Vulnerability("Aucune menace majeure détectée", "Faible"))
        return list
    }

    ////////////////////////////////////////////////

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

    private suspend fun startScanProcess(server: Server, repository: ServerRepository) {
        delay(3000) // Simulation du scan

        // Générer un score aléatoire entre 30 et 95 (pour éviter le 100% constant)
        val randomScore = (30..95).random()
        val vulnerabilities = generateVulnerabilities(randomScore)
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Mise à jour de l'interface
        binding.apply {
            progressBarScan.visibility = View.GONE
            tvScoreResult.visibility = View.VISIBLE
            tvScoreResult.text = "$randomScore%"

            tvVulnLabel.visibility = View.VISIBLE
            tvVulnDetails.visibility = View.VISIBLE
            // On construit le texte des vulnérabilités
            tvVulnDetails.text = vulnerabilities.joinToString("\n") { "• ${it.title} (${it.severity})" }

            btnBack.visibility = View.VISIBLE
        }

        // Mise à jour du serveur dans la base Room
        val updatedServer = server.copy(
            securityScore = randomScore,
            lastScanDate = date
        )
        repository.updateServer(updatedServer)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

 */

class ScanFragment : Fragment(R.layout.fragment_scan) {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var serverId: Int = -1

    // Data class pour les vulnérabilités
    data class Vulnerability(val title: String, val severity: String)

    private fun generateVulnerabilities(score: Int): List<Vulnerability> {
        val list = mutableListOf<Vulnerability>()
        if (score < 90) list.add(Vulnerability("Ports non sécurisés (SSH/Telnet)", "Élevée"))
        if (score < 70) list.add(Vulnerability("Certificat SSL expiré", "Moyenne"))
        if (score < 50) list.add(Vulnerability("OS obsolète", "Critique"))
        if (score < 30) list.add(Vulnerability("Mots de passe par défaut", "Critique"))
        if (list.isEmpty()) list.add(Vulnerability("Aucune menace majeure", "Faible"))
        return list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentScanBinding.bind(view)

        serverId = arguments?.getInt("SERVER_ID") ?: -1

        val db = AppDatabase.getDatabase(requireContext())
        val repository = ServerRepository(db.serverDao())

        // --- CORRECTION ICI ---
        viewLifecycleOwner.lifecycleScope.launch {
            // 1. On récupère d'abord les données réelles du serveur
            val server = repository.getServerById(serverId)

            if (server != null) {
                // On affiche les infos avant de scanner
                binding.tvServerNameScan.text = "Cible : ${server.serverName}"
                binding.tvServerIPScan.text = "IP : ${server.ipAddress}"
                binding.tvServerOSScan.text = "Système : ${server.os}"

                // 2. On lance le scan avec l'objet server récupéré
                startScanProcess(server, repository)
            } else {
                Toast.makeText(requireContext(), "Erreur : Serveur introuvable", Toast.LENGTH_SHORT).show()            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private suspend fun startScanProcess(server: Server, repository: ServerRepository) {
        delay(3000) // Simulation

        val randomScore = (30..99).random()
        val vulnerabilities = generateVulnerabilities(randomScore)
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        binding.apply {
            progressBarScan.visibility = View.GONE
            tvScoreResult.visibility = View.VISIBLE
            tvScoreResult.text = "Score : $randomScore%"

            tvVulnLabel.visibility = View.VISIBLE
            tvVulnDetails.visibility = View.VISIBLE
            tvVulnDetails.text = vulnerabilities.joinToString("\n") { "• ${it.title} (${it.severity})" }

            btnBack.visibility = View.VISIBLE
        }

        // 3. Sauvegarde le nouveau score dans la DB
        val updatedServer = server.copy(
            securityScore = randomScore,
            lastScanDate = date
        )
        repository.update(updatedServer) // Utilise update ou updateServer selon ton nommage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}