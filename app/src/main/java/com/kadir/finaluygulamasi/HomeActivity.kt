package com.kadir.finaluygulamasi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kadir.finaluygulamasi.AddListingActivity


class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListingsAdapter
    private val listings = mutableListOf<Listing>()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Firestore ve Firebase Auth başlatma
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // RecyclerView ve Adapter'ı başlatma
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListingsAdapter(listings)
        recyclerView.adapter = adapter

        // İlanları yükleme
        loadListings()
    }
    //Firestore veritabanından ilanları çekmek için kullanılan metod.
    private fun loadListings() {
        db.collection("Posts")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING) // Tarihe göre azalan sırada sıralama
            .get()
            .addOnSuccessListener { result ->
                listings.clear()
                for (document in result) {
                    try {
                        val listing = document.toObject(Listing::class.java).copy(
                            imageUrl = document.getString("downloadUrl") ?: "" // Firestore'daki `downloadUrl` alanını `imageUrl` ile eşleştir
                        )
                        listings.add(listing)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Menü oluşturma
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Menü tıklama olaylarını yakalama
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_listing -> {
                // İlan Ekleme Aktivitesine Geçiş
                val intent = Intent(this, AddListingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_logout -> {
                // Oturumu kapatma ve giriş ekranına yönlendirme
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
