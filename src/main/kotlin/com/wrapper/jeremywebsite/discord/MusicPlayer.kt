package com.wrapper.jeremywebsite.discord

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.javacord.api.DiscordApi
import org.javacord.api.audio.AudioConnection
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.event.message.MessageCreateEvent

class MusicPlayer {

  private var currentAudioConnection: AudioConnection? = null
  private val playerManager = DefaultAudioPlayerManager().also {
    it.registerSourceManager(YoutubeAudioSourceManager())
  }

  private val player: AudioPlayer = playerManager.createPlayer().also {
    // listen for track events, if track ends then queue up next song
    it.addListener { event ->
      if (event is TrackEndEvent) {
        trackScheduler.onTrackEnd(it, event.track, event.endReason)
      }
    }
  }
  private val trackScheduler: TrackScheduler = TrackScheduler(player)
  // Create an audio source and add it to the audio connection's queue
  private var source: LavaPlayerAudioSource? = null

  fun handleMessage(event: MessageCreateEvent) {
    if (source == null) {
      source = LavaPlayerAudioSource(event.api, player)
    }
    with(event) {
      when {
        // queue up a new song
        messageContent.split(" ").first() == "!play" || messageContent.split(" ").first() == "!p" -> {
          // if not enough arguments are provided then display an error message
          if (messageContent.split(" ").size < 2) {
            channel.sendMessage("You need to include a youtube link")
          } else {
            // search for the message author's connected voice channel
            messageAuthor.connectedVoiceChannel.map {
              // if the bot is currently connected to the channel then play the song
              if (it.isConnected(api.yourself) && currentAudioConnection != null) {
                playMusic(messageContent.split(" ")[1], channel)
              } else {
                // if the bot is not connected to the channel then connect and then play the song
                it.connect().thenAccept { audioConnection ->
                  currentAudioConnection = audioConnection
                  currentAudioConnection!!.setAudioSource(source)
                  playMusic(messageContent.split(" ")[1], channel)
                }
              }
            }
          }
        }
        messageContent == "!skip" || messageContent == "!s" -> {
          if (trackScheduler.queue.isEmpty()) {
            channel.sendMessage("Queue is empty")
            player.stopTrack()
          } else {
            trackScheduler.nextTrack()
          }
        }
        messageContent == "!currentSong" -> {
          channel.sendMessage(
            if (player.playingTrack != null) {
              player.playingTrack.info.title
            } else {
              "No song playing"
            }
          )
        }
        messageContent == "!disconnect" -> {
          player.destroy()
          trackScheduler.queue.clear()
          currentAudioConnection?.close()
        }
        else -> { /* do nothing */ }
      }
    }
  }

  private fun playMusic(searchURL: String, channel: TextChannel) {
    // load up new track/playlist
    try {
      playerManager.loadItem(searchURL, object : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack?) {
          if (track != null) {
            trackScheduler.queue(track, channel)
          }
        }

        override fun playlistLoaded(playlist: AudioPlaylist?) {
          if (playlist != null) {
            for (track in playlist.tracks) {
              trackScheduler.queue(track, channel)
            }
          }
        }

        override fun noMatches() {
          channel.sendMessage("No results found")
        }

        override fun loadFailed(p0: FriendlyException?) {
          channel.sendMessage("Failed to load, try again")
        }
      })
    } catch (e: Exception) {
      channel.sendMessage("An error occurred, try again")
    }
  }
}