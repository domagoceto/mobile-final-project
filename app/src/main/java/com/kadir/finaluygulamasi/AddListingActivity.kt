package com.kadir.finaluygulamasi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddListingActivity : AppCompatActivity() {

    //Değişkenler
    private lateinit var titleEditText: EditText
    private lateinit var areaEditText: EditText
    private lateinit var roomCountEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var publishButton: Button

    private var selectedImageUri: Uri? = null

    //FireBase Elmanları
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            findViewById<ImageView>(R.id.imageView).setImageURI(uri)
            Toast.makeText(this, "Resim başarıyla seçildi!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Resim seçilemedi!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_listing)

        //Kullanıcıdan alınan inputlar
        titleEditText = findViewById(R.id.titleEditText)
        areaEditText = findViewById(R.id.areaEditText)
        roomCountEditText = findViewById(R.id.roomCountEditText)
        priceEditText = findViewById(R.id.priceEditText)
        cityEditText = findViewById(R.id.cityEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        publishButton = findViewById(R.id.publishButton)

        selectImageButton.setOnClickListener { selectImageLauncher.launch("image/*") }
        publishButton.setOnClickListener { publishListing() }
    }

    //Yayınlama İşlemi
    private fun publishListing() {
        val title = titleEditText.text.toString().trim()
        val area = areaEditText.text.toString().trim()
        val roomCount = roomCountEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()

        if (title.isEmpty() || area.isEmpty() || roomCount.isEmpty() || price.isEmpty() || city.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun ve bir resim seçin!", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = auth.currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "Oturum açmamışsınız. Lütfen giriş yapın.", Toast.LENGTH_SHORT).show()
            return
        }
        //FireBase Yüklenmesi için referans
        val imageName = "${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child("images/$imageName")

        //Storage'a resim yükleme işlemi
        selectedImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveToFirestore(title, area, roomCount, price, city, downloadUrl.toString(), userEmail)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this, "Resim yüklenirken hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveToFirestore(
        title: String,
        area: String,
        roomCount: String,
        price: String,
        city: String,
        downloadUrl: String,
        userEmail: String
    ) {
        val listing = hashMapOf(
            "title" to title,
            "area" to area,
            "roomCount" to roomCount,
            "price" to price,
            "city" to city,
            "downloadUrl" to downloadUrl,
            "userEmail" to userEmail,
            "date" to Date()
        )

        db.collection("Posts").add(listing)
            .addOnSuccessListener {
                Toast.makeText(this, "İlan başarıyla yayımlandı!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Firestore hatası: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}
