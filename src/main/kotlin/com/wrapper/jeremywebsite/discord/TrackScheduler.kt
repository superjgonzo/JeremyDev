package com.wrapper.jeremywebsite.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.user.User
import java.awt.Color
import java.util.concurrent.LinkedBlockingDeque

class TrackScheduler(private val audioPlayer: AudioPlayer) : AudioEventAdapter() {

  val queue: LinkedBlockingDeque<QueueItem> = LinkedBlockingDeque()
  private lateinit var currentChannel: TextChannel

  fun nextTrack() {
    val nextItem = queue.poll()
    if (nextItem == null) {
      currentChannel.sendMessage(
        EmbedBuilder()
          .setTitle("Queue is empty")
          .setColor(Color.RED)
      )
    } else {
      currentChannel.sendMessage(
        EmbedBuilder()
          .setAuthor(nextItem.queuedBy.nicknameMentionTag, null, nextItem.queuedBy.avatar)
          .setTitle(nextItem.track.info.title)
          .setDescription(nextItem.track.info.author)
          .addField("Songs in Queue", queue.size.toString())
          .setImage("https://img.youtube.com/vi/${nextItem.track.info.identifier}/maxresdefault.jpg")
          .addField("URL", nextItem.track.info.uri)
          .setAuthor(nextItem.queuedBy)
          .setColor(Color.GREEN)
      )
      audioPlayer.startTrack(nextItem.track, false)
    }
  }

  fun queueNext(track: AudioTrack, user: User) {
    // if a song is not already playing
    if (!audioPlayer.startTrack(track, true)) {
      if (queue.offerFirst(QueueItem(track, user))) {
        currentChannel.sendMessage(
          EmbedBuilder()
            .setTitle("Queueing Up Next: ")
            .setTitle(track.info.title)
            .setDescription( "Songs in queue: " + queue.size)
            .setColor(Color.LIGHT_GRAY)
        )
      }
    } else {
      currentChannel.sendMessage(
        EmbedBuilder()
          .setAuthor(user.nicknameMentionTag, null, user.avatar)
          .setTitle(track.info.title)
          .setDescription(track.info.author)
          .addField("Songs in Queue", queue.size.toString())
          .setImage("https://img.youtube.com/vi/${track.info.identifier}/maxresdefault.jpg")
          .addField("URL", track.info.uri)
          .setAuthor(user)
          .setColor(Color.BLUE)
      )
    }
  }

  fun queue(track: AudioTrack, channel: TextChannel, user: User) {
    currentChannel = channel
    // if a song is not already playing
    if (!audioPlayer.startTrack(track, true)) {
      if (queue.add(QueueItem(track, user))) {
        currentChannel.sendMessage(
          EmbedBuilder()
            .setTitle("Queueing Up: " + track.info.title)
            .setDescription( "Songs in queue: " + queue.size)
            .setColor(Color.LIGHT_GRAY)
        )
//        channel.sendMessage("Queueing Up: " + track.info.title + "\nsongs in queue: " + queue.size)
      }
    } else {
      currentChannel.sendMessage(
        EmbedBuilder()
          .setAuthor(user.nicknameMentionTag, null, user.avatar)
          .setTitle(track.info.title)
          .setDescription(track.info.author)
          .setImage("https://img.youtube.com/vi/${track.info.identifier}/maxresdefault.jpg")
          .addField("URL", track.info.uri)
          .setAuthor(user)
          .setColor(Color.BLUE)
      )
    }
  }

  override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
    if (endReason == AudioTrackEndReason.LOAD_FAILED) {
     currentChannel.sendMessage("Error Loading song: " + track?.info?.title + "\n skipping to the next song")
    }

    if (endReason?.mayStartNext == true) {
      if (queue.isNotEmpty()) {
        nextTrack()
      }
    }
  }
}