package com.example.voc_security_mob.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.voc_security_mob.R
import com.example.voc_security_mob.databinding.ActivityAdminDashboardBinding
import com.example.voc_security_mob.ui.analyst.ServerListFragment
import com.example.voc_security_mob.ui.auth.LoginActivity

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Charger le fragment par défaut (Home)
        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_users -> loadFragment(UserListFragment())
               // R.id.nav_add -> loadFragment(AddUserFragment()) //suprimé
                R.id.nav_assets -> loadFragment(ServerListFragment())
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                //showLogoutDialog()
                   //false // On ne change pas d'onglet visuellement pour le logout
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Déconnexion")
            .setMessage("Voulez-vous quitter ?")
            .setPositiveButton("Oui") { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.setNegativeButton("Non", null).show()
    }
}