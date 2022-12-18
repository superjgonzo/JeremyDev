package com.wrapper.jeremywebsite.discord

import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType

class CommandFactory(private val api: DiscordApi) {

  companion object {
    const val PLAY = "play"
    const val PLAY_NEXT = "playnext"
    const val SKIP = "skip"
    const val ASCEND = "ascend"
    const val BEDIGA = "bediga"
    const val CURRENT_SONG = "currentsong"
    const val DISCONNECT = "disconnect"
    const val CLEAR = "clear"
  }

  fun createCommands() {
    SlashCommand.with(CURRENT_SONG, "Gets information for the current song")
      .createGlobal(api)
      .join()

    SlashCommand.with(
      PLAY,
      "play a song",
      listOf(
        SlashCommandOption.create(SlashCommandOptionType.STRING, "song", "Youtube url")
      )
    )
      .createGlobal(api)
      .join()

    SlashCommand.with(
      PLAY_NEXT,
      "Skip the Queue and play this song next",
      listOf(
        SlashCommandOption.create(SlashCommandOptionType.STRING, "song", "Youtube url")
      )
    )
      .createGlobal(api)
      .join()

    SlashCommand.with(SKIP, "Skip the current song that is playing")
      .createGlobal(api)
      .join()

    SlashCommand.with(ASCEND, "ASCEND MY BROTHER")
      .createGlobal(api)
      .join()

    SlashCommand.with(BEDIGA, "Play the Bediga Playlist")
      .createGlobal(api)
      .join()

    SlashCommand.with(DISCONNECT, "Disconnect Bediga Bot from the Channel")
      .createGlobal(api)
      .join()

    SlashCommand.with(CLEAR, "Clear the playlist")
      .createGlobal(api)
      .join()
  }
}