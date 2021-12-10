package com.wrapper.jeremywebsite.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.javacord.api.entity.channel.TextChannel
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class TrackScheduler(private val audioPlayer: AudioPlayer) : AudioEventAdapter() {

  val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue()
  private lateinit var currentChannel: TextChannel

  fun nextTrack() {
    val nextTrack = queue.poll()
    if (nextTrack == null) {
      currentChannel.sendMessage("Queue is empty")
    } else {
      currentChannel.sendMessage("Now Playing: " + nextTrack.info.title)
      audioPlayer.startTrack(nextTrack, false)
    }
  }

  fun queue(track: AudioTrack, channel: TextChannel) {
    currentChannel = channel
    // if a song is not already playing
    if (!audioPlayer.startTrack(track, true)) {
      if (queue.add(track)) {
        channel.sendMessage("Queueing Up: " + track.info.title + "\nsongs in queue: " + queue.size)
      }
    } else {
      channel.sendMessage("Starting Track: " + track.info.title)
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