package com.example.ass2

data class YoutubeResponse(
    val items: List<VideoItem>
)

data class VideoItem(
    val id: VideoId,
    val snippet: VideoSnippet
)

data class VideoId(
    val videoId: String?
)

data class VideoSnippet(
    val title: String,
    val description: String,
    val publishedAt: String,
    val channelTitle: String,
    val thumbnails: VideoThumbnails
)

data class VideoThumbnails(
    val high: ThumbnailUrl
)

data class ThumbnailUrl(
    val url: String
)