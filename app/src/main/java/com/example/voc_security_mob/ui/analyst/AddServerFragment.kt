package com.example.voc_security_mob.ui.analyst

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.local.entities.Server
import com.example.voc_security_mob.data.repository.ServerRepository
import com.example.voc_security_mob.databinding.FragmentAddServerBinding
import kotlinx.coroutines.launch

class AddServerFragment : Fragment(R.layout.fragment_add_server) {

    private var _binding: FragmentAddServerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddServerBinding.bind(view)

        val db = AppDatabase.getDatabase(requireContext())
        val repository = ServerRepository(db.serverDao())

        binding.btnSaveServer.setOnClickListener {
            // 1. Récupérer l'organisation depuis la session
            val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val userOrg = sharedPref.getString("USER_ORG", "Inconnue") ?: "Inconnue"

            // 2. Créer l'objet Server avec la bonne organisation
            val server = Server(
                serverName = binding.etServerName.text.toString(),
                ipAddress = binding.etIpAddress.text.toString(),
                os = binding.spinnerOS.selectedItem.toString(),
                criticality = binding.spinnerCriticality.selectedItem.toString(),
                organizationOwner = userOrg // DYNAMIQUE  !
            )

            lifecycleScope.launch {
                repository.insert(server)
                Toast.makeText(context, "Serveur enregistré pour $userOrg", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}