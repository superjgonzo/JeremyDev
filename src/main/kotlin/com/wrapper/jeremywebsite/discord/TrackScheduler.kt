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

  fun nextTrack() {
    audioPlayer.startTrack(queue.poll(), false)
  }

  fun queue(track: AudioTrack, channel: TextChannel) {
    // if a song is not already playing
    if (!audioPlayer.startTrack(track, true)) {
      channel.sendMessage("Queueing up: " + track.info.title + "\nsongs in queue: " + (queue.size + 1))
      queue.add(track)
    }
  }

  override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
    if (endReason?.mayStartNext == true) {
      if (queue.isNotEmpty()) {
        nextTrack()
      }
    }
  }
}