package com.example.appcontact_1

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contactList: List<AboutContact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // Для хранения контакта
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val contactImageView: ImageView = itemView.findViewById(R.id.contactImageView)

        init {
            // Звонок по нажатию на карточку
            itemView.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneTextView.text}"))
                it.context.startActivity(dialIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        // Макет элемента
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_rv_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        // Текущий контакт
        val currentContact = contactList[position]
        holder.nameTextView.text = currentContact.name
        holder.phoneTextView.text = currentContact.phone
        holder.contactImageView.setImageResource(R.drawable.avatar) // Аватарка
    }

    override fun getItemCount(): Int = contactList.size
}

/*
Аватарки контактов дефолт или должен был взять из системы при вызове?
 Я оставил дефолт, но изменить не сложно.
 */