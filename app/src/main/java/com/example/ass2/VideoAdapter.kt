package com.example.ass2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(private var videos: List<VideoItem>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvChannelAndDate: TextView = view.findViewById(R.id.tvChannelAndDate)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        val snippet = video.snippet

        holder.tvTitle.text = snippet.title
        holder.tvDescription.text = snippet.description
        holder.tvChannelAndDate.text = "${snippet.channelTitle} • ${snippet.publishedAt.take(10)}"

        Glide.with(holder.itemView.context)
            .load(snippet.thumbnails.high.url)
            .placeholder(android.R.color.darker_gray)
            .into(holder.ivThumbnail)
    }

    override fun getItemCount(): Int = videos.size

    fun updateData(newVideos: List<VideoItem>) {
        this.videos = newVideos
        notifyDataSetChanged()
    }
}