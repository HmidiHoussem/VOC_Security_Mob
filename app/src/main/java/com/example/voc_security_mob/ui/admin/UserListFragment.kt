package com.example.voc_security_mob.ui.admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityUserListBinding
import kotlinx.coroutines.launch



class UserListFragment : Fragment(R.layout.activity_user_list) { // On lie le XML ici

    private var _binding: ActivityUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserAdapter

    // onCreateView : On crée la vue (automatique via le constructeur ci-dessus)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityUserListBinding.bind(view)

        // 1. Setup RecyclerView (Comme dans ton ancienne Activity)
        adapter = UserAdapter(emptyList())
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        // 2. Accès Database (On utilise requireContext() au lieu de 'this')
        val db = AppDatabase.getDatabase(requireContext())
        val repository = UserRepository(db.userDao())



        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "")
        val org = sharedPref.getString("USER_ORG", "").toString()

        viewLifecycleOwner.lifecycleScope.launch {
            if (role == "ADMIN") {
                repository.allUsers.collect { list -> adapter.updateData(list) }
            } else {
                // Le Manager ne voit que les gens de sa société (hors Admins)
                repository.getUsersByOrganization(org).collect { list -> adapter.updateData(list) }
            }
        }
        /*
        // 3. Observation des données
        lifecycleScope.launch {
            repository.allUsers.collect { users ->
                adapter.updateData(users)
            }
        }

 */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important pour éviter les fuites de mémoire
    }
}

