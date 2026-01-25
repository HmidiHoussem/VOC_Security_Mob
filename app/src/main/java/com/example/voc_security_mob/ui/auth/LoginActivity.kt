package com.example.voc_security_mob.ui.auth



import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityLoginBinding
import com.example.voc_security_mob.ui.admin.AddUserActivity
import com.example.voc_security_mob.ui.admin.AdminDashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation manuelle simple pour le Sprint 1
        val db = AppDatabase.getDatabase(this)
        val repository = UserRepository(db.userDao())
        viewModel = LoginViewModel(repository)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.login(email) { user ->
                if (user != null && user.password == password) {
                    Toast.makeText(this, "Bienvenue ${user.name} (${user.role})", Toast.LENGTH_SHORT).show()
                    // Ici on redirigera vers l'écran Admin ou Analyste selon le rôle
                    if (user.role == "ADMIN") {

                        val intent = Intent(this, AdminDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Accès réservé aux admins pour le moment", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}