package com.example.voc_security_mob.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.local.entities.User
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityAddUserBinding

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val repository = UserRepository(db.userDao())
        viewModel = AdminViewModel(repository)

        binding.btnSaveUser.setOnClickListener {
            val newUser = User(
                name = binding.etNewName.text.toString(),
                email = binding.etNewEmail.text.toString(),
                password = binding.etNewPassword.text.toString(),
                role = binding.spinnerRole.selectedItem.toString(),
                organizationName = binding.etNewOrganization.text.toString()
            )

            viewModel.addUser(newUser)
            Toast.makeText(this, "Utilisateur ajouté !", Toast.LENGTH_SHORT).show()
            finish() // Retour à l'écran précédent
        }
    }
}