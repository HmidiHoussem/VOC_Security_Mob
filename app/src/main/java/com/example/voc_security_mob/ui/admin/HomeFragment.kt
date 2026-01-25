package com.example.voc_security_mob.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // Initialisation Database & Repository
        val db = AppDatabase.getDatabase(requireContext())
        val repository = UserRepository(db.userDao())

        // Observation du nombre d'utilisateurs
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userCount.collect { count ->
                binding.tvCountUsers.text = count.toString()
            }
        }

        // Observation du nombre d'organisations
        viewLifecycleOwner.lifecycleScope.launch {
            repository.organizationCount.collect { count ->
                binding.tvCountOrgs.text = count.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}