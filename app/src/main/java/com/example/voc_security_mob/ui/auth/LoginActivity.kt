package com.example.voc_security_mob.ui.auth



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voc_security_mob.data.local.AppDatabase
import com.example.voc_security_mob.data.repository.UserRepository
import com.example.voc_security_mob.databinding.ActivityLoginBinding
import com.example.voc_security_mob.ui.admin.AddUserActivity
import com.example.voc_security_mob.ui.admin.AdminDashboardActivity
import com.example.voc_security_mob.ui.common.DashboardActivity
import kotlinx.coroutines.launch

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

            /*
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

             */



            // Dans ton LoginActivity, lors du clic sur le bouton se connecter :
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.login(email, password) { user ->
                if (user != null) {
                    // --- SAUVEGARDE DE LA SESSION ---
                    val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USER_ID", user.id.toString())
                        putString("USER_NAME", user.name)
                        putString("USER_EMAIL", user.email)
                        putString("USER_ROLE", user.role) // Ex: "ADMIN" ou "ANALYSTE"
                        putString("USER_ORG", user.organizationName)
                        apply()
                    }

                    // --- REDIRECTION VERS LE DASHBOARD HYBRIDE ---
                    Toast.makeText(this, "Bienvenue ${user.name}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
                }
            }
            // Dans la méthode de validation du Login (ex: btnLogin.setOnClickListener)
        }
    }
}