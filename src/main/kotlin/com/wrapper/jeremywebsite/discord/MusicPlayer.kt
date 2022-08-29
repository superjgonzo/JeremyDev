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
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.user.User
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.Interaction

class MusicPlayer(val api: DiscordApi) {
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

  init {
    source = LavaPlayerAudioSource(api, player)
  }

  fun handleSlashCommand(event: SlashCommandCreateEvent) {
    when (event.slashCommandInteraction.commandName) {
      CommandFactory.PLAY -> {
        // ensure the bot is connected to a voice channel
        if (checkIfConnected(event)) {
          playMusic(
            event.interaction,
            event.slashCommandInteraction.arguments.first().stringValue.get(),
            event.interaction.channel.get(),
            event.interaction.user
          )
        }
      }
      CommandFactory.PLAY_NEXT -> {
        // ensure the bot is connected to a voice channel
        if (checkIfConnected(event)) {
          playMusic(
            interaction = event.interaction,
            searchURL = event.slashCommandInteraction.arguments.first().stringValue.get(),
            channel = event.interaction.channel.get(),
            user = event.interaction.user,
            shouldPlayNext = true
          )
        }
      }
      CommandFactory.ASCEND -> {
        // ensure the bot is connected to a voice channel
        if (checkIfConnected(event)) {
          playMusic(
            interaction = event.interaction,
            searchURL = "https://www.youtube.com/watch?v=t6isux5XWH0&t=80s",
            channel = event.interaction.channel.get(),
            user = event.interaction.user,
            shouldPlayNext = true,
            shouldPlayNow = true,
            customInteractionResponse = {
              event.interaction
                .createImmediateResponder()
                .setContent("AMENO!")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
            }
          )
        }
      }
      CommandFactory.BEDIGA -> {
        // ensure the bot is connected to a voice channel
        if (checkIfConnected(event)) {
          playMusic(
            event.interaction,
            "https://www.youtube.com/playlist?list=PLG6VjfkF37Qx-SwOWKOTJ-7bBx7KZIObS",
            event.interaction.channel.get(),
            event.interaction.user
          )
        }
      }
      CommandFactory.CURRENT_SONG -> {
        val content = if (player.playingTrack != null) {
          player.playingTrack.info.title +
            "\n${player.playingTrack.duration}" +
            "\n${player.playingTrack.position}"
        } else {
          "No song playing"
        }

        event.interaction
          .createImmediateResponder()
          .setContent(content)
          .setFlags(MessageFlag.EPHEMERAL)
          .respond()
      }
      CommandFactory.SKIP -> {
        if (trackScheduler.queue.isEmpty()) {
          player.stopTrack()
        }
        trackScheduler.nextTrack()

        event.interaction
          .createImmediateResponder()
          .setContent("Skipping track")
          .respond()
      }
      CommandFactory.DISCONNECT -> {
        event.interaction.createImmediateResponder().setContent("Goodbye My Sweet Child").respond()
        player.destroy()
        trackScheduler.queue.clear()
        currentAudioConnection?.close()
      }
      else -> event.interaction
        .createImmediateResponder()
        .setContent("Who asked? \n- Dima")
        .respond()
    }
  }

  private fun playMusic(
    interaction: Interaction,
    searchURL: String,
    channel: TextChannel,
    user: User,
    shouldPlayNext: Boolean = false,
    shouldPlayNow: Boolean = false,
    customInteractionResponse: (() -> Unit)? = null
  ) {
    // load up new track/playlist
    try {
      playerManager.loadItem(searchURL, object : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack?) {
          if (track != null) {
            if (shouldPlayNext && trackScheduler.queue.isNotEmpty()) {
              trackScheduler.queueNext(track, user)
            } else {
              trackScheduler.queue(track, channel, user)
            }
          }

          if (shouldPlayNow) {
            trackScheduler.nextTrack()
          }

          customInteractionResponse?.let {
            it()
          } ?: interaction
            .createImmediateResponder()
            .setContent("Song Added!")
            .setFlags(MessageFlag.EPHEMERAL)
            .respond()
        }

        override fun playlistLoaded(playlist: AudioPlaylist?) {
          if (playlist != null) {
            val longInteractionWait = interaction.respondLater(true)

            val listOfTracks = playlist.tracks.also { it.shuffle() }
            for (track in listOfTracks) {
              trackScheduler.queue(track, channel, user)
            }

            longInteractionWait
              .thenAccept {
                it.setContent("All tracks added!")
                  .setFlags(MessageFlag.EPHEMERAL)
                  .update()
              }
          }
        }

        override fun noMatches() {
          interaction
            .createImmediateResponder()
            .setContent("No Matches Found :(")
            .setFlags(MessageFlag.EPHEMERAL)
            .respond()
        }

        override fun loadFailed(p0: FriendlyException?) {
          interaction
            .createImmediateResponder()
            .setContent("Failed to load, try again!")
            .setFlags(MessageFlag.EPHEMERAL)
            .respond()
        }
      })
    } catch (e: Exception) {
      interaction
        .createImmediateResponder()
        .setContent("An error occurred, try again")
        .setFlags(MessageFlag.EPHEMERAL)
        .respond()
    }
  }

  private fun checkIfConnected(event: SlashCommandCreateEvent): Boolean {
    val connectedVoiceCHannels = event.interaction.user.connectedVoiceChannels

    if (connectedVoiceCHannels.map {
      if (!it.isConnected(api.yourself) && currentAudioConnection == null) {
        // if the bot is not connected to the channel then connect and then play the song
        it.connect().thenAccept { audioConnection ->
          currentAudioConnection = audioConnection
          currentAudioConnection!!.setAudioSource(source)
        }
      }
    }.isEmpty()) {
      event.interaction
        .createImmediateResponder()
        .setContent("Not connected to a voice channel")
        .setFlags(MessageFlag.EPHEMERAL)
        .respond()
    }

    return connectedVoiceCHannels.isNotEmpty()
  }
}