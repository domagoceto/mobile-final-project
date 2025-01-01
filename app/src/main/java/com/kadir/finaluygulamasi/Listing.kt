package com.kadir.finaluygulamasi

data class Listing(
    val title: String = "",
    val area: String = "",
    val roomCount: String = "",
    val price: String = "",
    val city: String = "",
    val imageUrl: String = "",
    val date: com.google.firebase.Timestamp? = null ,
    val userEmail: String = ""
)
