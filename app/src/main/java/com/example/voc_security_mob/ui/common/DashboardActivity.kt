package com.example.voc_security_mob.ui.common

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.voc_security_mob.R
import com.example.voc_security_mob.databinding.ActivityDashboardBinding
import com.example.voc_security_mob.ui.admin.AddUserFragment
import com.example.voc_security_mob.ui.admin.HomeFragment
import com.example.voc_security_mob.ui.admin.ProfileFragment
import com.example.voc_security_mob.ui.admin.UserListFragment
import com.example.voc_security_mob.ui.analyst.ServerListFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. RÉCUPÉRER LES INFOS DE SESSION
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val role = sharedPref.getString("USER_ROLE", "ANALYSTE") ?: "ANALYSTE"

        // 2. CONFIGURER L'INTERFACE SELON LE RÔLE
        configureUIByRole(role)



        // 3. CHARGER LE FRAGMENT INITIAL
        if (savedInstanceState == null) {
            // Un Admin arrive sur les Stats, un Analyste arrive sur ses Serveurs
            val initialFragment = if (role == "ADMIN") HomeFragment() else ServerListFragment()
            loadFragment(initialFragment)
        }



        // 4. GESTION DES CLICS
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_users -> loadFragment(UserListFragment())
                R.id.nav_add -> loadFragment(AddUserFragment())
                R.id.nav_assets -> loadFragment(ServerListFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun configureUIByRole(role: String) {
        val menu = binding.bottomNavigation.menu
       if (role == "ANALYSTE") {
            // On cache l'onglet "Utilisateurs" pour l'analyste
            menu.findItem(R.id.nav_users).isVisible = false

        }
        /*else {
            // On cache l'onglet "Serveurs" pour l'admin
            menu.findItem(R.id.nav_assets).isVisible = false
        }

         */
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}