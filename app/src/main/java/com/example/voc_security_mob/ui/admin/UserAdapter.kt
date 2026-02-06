package com.example.voc_security_mob.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voc_security_mob.R
import com.example.voc_security_mob.data.local.entities.User

class UserAdapter(private var users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvUserName)
        val email: TextView = view.findViewById(R.id.tvUserEmail)
        val role: TextView = view.findViewById(R.id.tvUserRole)
        val organization: TextView = view.findViewById(R.id.tvUserOrg)
    }

    fun getUserAt(position: Int): User {
        return users[position]
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.email.text = user.email
        holder.role.text = user.role
        holder.organization.text = "🏢 ${user.organizationName}"
    }

    override fun getItemCount() = users.size

    // Pour mettre à jour la liste quand on ajoute un utilisateur
    fun updateData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

}