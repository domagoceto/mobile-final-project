package com.kadir.finaluygulamasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso



class ListingsAdapter(private val listings: List<Listing>) :
    RecyclerView.Adapter<ListingsAdapter.ListingViewHolder>() {

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val areaTextView: TextView = itemView.findViewById(R.id.areaTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listing_item, parent, false)
        return ListingViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listing = listings[position]
        holder.titleTextView.text = listing.title
        holder.areaTextView.text = "Metrekare: ${listing.area}, Oda: ${listing.roomCount}"
        holder.priceTextView.text = "Fiyat: ${listing.price} TL"
        holder.cityTextView.text = "Şehir: ${listing.city}"
        holder.emailTextView.text = "İlan Sahibi: ${listing.userEmail}"
        Picasso.get().load(listing.imageUrl).into(holder.imageView)
        //Picasso kütüphanesi kullanılarak, her bir listing için belirlenen
        // imageUrl'den görsel yüklenir ve ImageView'a yerleştirilir.
    }

    override fun getItemCount(): Int {
        return listings.size
    }
}

