package com.farizz.dicodingevent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farizz.dicodingevent.database.FavoriteEvent

class FavoriteAdapter(
    private val favoriteEvents: List<FavoriteEvent>,
    private val onItemClick: (FavoriteEvent) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.tv_event_name)
        val eventImage: ImageView = itemView.findViewById(R.id.img_event)

        init {
            itemView.setOnClickListener {
                onItemClick(favoriteEvents[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.eventName.text = favoriteEvents[position].name

        Glide.with(holder.itemView.context)
            .load(favoriteEvents[position].imageLogo)
            .into(holder.eventImage)
    }

    override fun getItemCount() = favoriteEvents.size
}