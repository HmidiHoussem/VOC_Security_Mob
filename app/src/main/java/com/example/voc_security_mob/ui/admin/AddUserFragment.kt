package com.example.voc_security_mob.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.local.entities.User
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityAddUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddUserFragment : Fragment(R.layout.activity_add_user) {
    private var _binding: ActivityAddUserBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityAddUserBinding.bind(view)

        val db = AppDatabase.getDatabase(requireContext())
        val repo = UserRepository(db.userDao())

        binding.btnSaveUser.setOnClickListener {
            val user = User(
                name = binding.etNewName.text.toString(),
                email = binding.etNewEmail.text.toString(),
                password = binding.etNewPassword.text.toString(),
                role = binding.spinnerRole.selectedItem.toString(),
                organizationName = binding.etNewOrganization.text.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                repo.insert(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Utilisateur ajouté !", Toast.LENGTH_SHORT).show()
                    // Retour automatique à la liste après ajout
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container, UserListFragment()).commit()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}