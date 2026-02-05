package com.example.voc_security_mob.ui.admin

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.ServerRepository
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    // View Binding pour accéder aux composants XML sans utiliser findViewById
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // --- INITIALISATION DES DONNÉES ---
        val db = AppDatabase.getDatabase(requireContext())
        val userRepo = UserRepository(db.userDao())
        val serverRepo = ServerRepository(db.serverDao())

        // Récupération de la session utilisateur stockée lors du Login
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "ANALYSTE") ?: "ANALYSTE"
        val userName = sharedPref.getString("USER_NAME", "Utilisateur")
        val userOrg = sharedPref.getString("USER_ORG", "") ?: ""

        // Personnalisation du message de bienvenue
        binding.tvWelcome.text = "Bonjour, $userName 👋"

        // --- OBSERVATION RÉACTIVE (FLOWS) ---
        // lifecycleScope.launch permet de surveiller la base de données en arrière-plan
        viewLifecycleOwner.lifecycleScope.launch {

            if (role == "ADMIN") {
                // L'ADMIN voit les statistiques globales de toute l'application
                launch { userRepo.userCount.collect { binding.tvCountUsers.text = it.toString() } }
                launch { userRepo.organizationCount.collect { binding.tvCountOrgs.text = it.toString() } }
                launch { serverRepo.totalServersCount.collect { binding.tvCountScans.text = it.toString() } }
                launch { serverRepo.criticalAlertsCount.collect { binding.tvCountAlerts.text = it.toString() } }
                launch { serverRepo.globalScore.collect { updateGlobalScoreUI(it ?: 0.0) } }
            } else {
                // MANAGER/ANALYSTE voient uniquement les données de leur propre société (userOrg)
                binding.tvCountOrgs.text = "1" // Ils ne gèrent que leur organisation

                // On utilise ici les fonctions filtrées par Org que nous avons créées
                launch { userRepo.getUserCountByOrg(userOrg).collect { it -> binding.tvCountUsers.text = it.toString() } }
                launch { serverRepo.getServerCountByOrg(userOrg).collect { it -> binding.tvCountScans.text = it.toString() } }
                launch { serverRepo.getCriticalAlertsCountByOrg(userOrg).collect { it -> binding.tvCountAlerts.text = it.toString() } }
                launch { serverRepo.getOrgGlobalScore(userOrg).collect { score -> updateGlobalScoreUI(score ?: 0.0) } }
            }
        }

        // --- ACTION : QUICK SCAN ---
        binding.btnQuickScan.setOnClickListener {
            simulateQuickScan(serverRepo, role, userOrg)
        }
    }

    /**
     * Simule une analyse de sécurité en mettant à jour les scores des serveurs.
     * Cette fonction démontre la réactivité de l'application.
     */
    private fun simulateQuickScan(serverRepo: ServerRepository, role: String, org: String) {
        // Désactivation du bouton pour éviter les clics multiples pendant le traitement
        binding.btnQuickScan.isEnabled = false
        binding.btnQuickScan.text = "Analyse en cours..."

        viewLifecycleOwner.lifecycleScope.launch {
            // Simulation d'un délai réseau/calcul de 1.5 seconde
            delay(1500)

            // Récupération de la liste des serveurs selon les droits
            val servers = serverRepo.getServersListSync(role, org)

            if (servers.isNotEmpty()) {
                servers.forEach { server ->
                    // On attribue un nouveau score aléatoire pour simuler l'analyse
                    val newScore = (20..100).random()
                    val updated = server.copy(
                        securityScore = newScore,
                        lastScanDate = "05/02/2026" // Date du jour
                    )
                    // La mise à jour en base déclenchera automatiquement les Flows du Dashboard
                    serverRepo.update(updated)
                }
                Toast.makeText(context, "Scan terminé pour $org", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Aucun serveur à analyser", Toast.LENGTH_SHORT).show()
            }

            // Réactivation de l'interface
            binding.btnQuickScan.isEnabled = true
            binding.btnQuickScan.text = "Lancer un Scan Rapide"
        }
    }

    /**
     * Met à jour l'affichage du score global (Texte + Barre de progression + Couleur)
     */
    private fun updateGlobalScoreUI(score: Double) {
        val finalScore = score.toInt()
        binding.tvGlobalScore.text = "$finalScore%"
        binding.progressScore.progress = finalScore

        // Logique de couleur : Vert si >= 75%, Jaune si >= 50%, sinon Rouge
        val color = when {
            finalScore >= 75 -> android.graphics.Color.parseColor("#4CAF50")
            finalScore >= 50 -> android.graphics.Color.parseColor("#FFC107")
            else -> android.graphics.Color.parseColor("#F44336")
        }

        // Applique la couleur à la barre de progression
        binding.progressScore.setProgressTintList(android.content.res.ColorStateList.valueOf(color))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Libération de la mémoire pour éviter les fuites
    }
}

/* package com.example.voc_security_mob.ui.admin

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.ServerRepository
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // 1. Initialisation Database & Repositories
        val db = AppDatabase.getDatabase(requireContext())
        val userRepo = UserRepository(db.userDao())
        val serverRepo = ServerRepository(db.serverDao())

        // 2. Récupération des infos de session
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "ANALYSTE")
        val userName = sharedPref.getString("USER_NAME", "Utilisateur")
        val userOrg = sharedPref.getString("USER_ORG", "") ?: ""

        // 3. UI Personnalisée (Bienvenue)
        binding.tvWelcome.text = "Bonjour, $userName 👋"

        // 4. Observation des statistiques (Logique des Rôles)
        viewLifecycleOwner.lifecycleScope.launch {
            if (role == "ADMIN") {
                // L'ADMIN voit tout le monde
                launch { userRepo.userCount.collect { binding.tvCountUsers.text = it.toString() } }
                launch { userRepo.organizationCount.collect { binding.tvCountOrgs.text = it.toString() } }
                launch {
                    serverRepo.globalScore.collect { score ->
                        updateGlobalScoreUI(score ?: 0.0)
                    }
                }
            } else {
                // MANAGER & ANALYSTE voient uniquement leur ORG
                // Note: Tu peux ajouter une fonction userCountByOrg dans UserRepository si besoin
                binding.tvCountOrgs.text = "1" // Ils ne voient que leur propre société
                launch {
                    serverRepo.getOrgGlobalScore(userOrg).collect { score ->
                        updateGlobalScoreUI(score ?: 0.0)
                    }
                }
            }

            // Commun à tous (Alertes et Total)
            launch { serverRepo.criticalAlertsCount.collect { binding.tvCountAlerts.text = it.toString() } }
            launch { serverRepo.totalServersCount.collect { binding.tvCountScans.text = it.toString() } }
        }

        // 5. Bouton Quick Scan
        binding.btnQuickScan.setOnClickListener {
            // Logique pour lancer un scan ou naviguer vers la page scan
        }
    }

    private fun updateGlobalScoreUI(score: Double) {
        val finalScore = score.toInt()
        binding.tvGlobalScore.text = "$finalScore%"
        binding.progressScore.progress = finalScore

        // Optionnel : Changer la couleur de la progress bar selon le score
        if (finalScore < 50) {
            binding.progressScore.setProgressTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

 */