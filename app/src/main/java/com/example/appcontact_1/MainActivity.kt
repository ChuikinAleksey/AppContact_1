package com.example.appcontact_1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/*
Сейчас код берет первый номер из базы android, в ТЗ мне было не совсем понянтно по какой логтке
производить звонок, то есть брать мне обязательно мобильный номер телефон (даже если в карточке
указан например рабочий) или я должен использовать тот номер для звонка, который указан в карточке?...
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noContactsMessage: TextView
    private val contactList = mutableListOf<AboutContact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.contactRecyclerView)
        noContactsMessage = findViewById(R.id.noContactsMessage)

        // Тут RecyclerView с линейным расположением
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Запрос разрешения на вызов контакта
        if (isPermissionGranted()) {
            fetchContacts()
        } else {
            requestContactPermissions()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 101)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchContacts()
            }
        }
    }

    private fun fetchContacts() {
        contactList.clear() // Очиститьсписок перед загрузкой новых контактов

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        )

        cursor?.use { cur ->
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) ?: "Unnamed"
                val hasPhone = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhone > 0) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id),
                        null
                    )

                    phoneCursor?.use { pc ->
                        if (pc.moveToFirst()) {
                            val phone = pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactList.add(AboutContact(name, phone))
                        }
                    }
                }
            }
        }

        // Контакты не найдены, предупреждение
        if (contactList.isEmpty()) {
            noContactsMessage.visibility = TextView.VISIBLE
        } else {
            noContactsMessage.visibility = TextView.GONE
            val adapter = ContactAdapter(contactList)
            recyclerView.adapter = adapter
        }
    }
}