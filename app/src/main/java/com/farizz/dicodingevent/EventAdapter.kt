package com.farizz.dicodingevent

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farizz.dicodingevent.data.response.ListEventsItem

class EventAdapter(
    private val events: List<ListEventsItem?>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName = itemView.findViewById<TextView>(R.id.tv_event_name)
        private val eventImage = itemView.findViewById<ImageView>(R.id.img_event)

        fun bind(event: ListEventsItem?) {
            event?.let {
                eventName.text = it.name

                Glide.with(itemView.context)
                    .load(it.imageLogo ?: it.mediaCover)
                    .into(eventImage)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id) //
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = events.size
}