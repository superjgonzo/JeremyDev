package com.wrapper.jeremywebsite.discord

import com.wrapper.jeremywebsite.GoogleCloudRepository
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class DiscordRepository @Autowired constructor(googleCloudRepository: GoogleCloudRepository) {

  private val discordToken = googleCloudRepository.accessDiscordToken()
// uncomment an put discord token
//  private val discordToken = ""

  // UNCOMMENT OUT IF YOU WANT THE DISCORD BOT TO RUN WHEN THE WEBSITE IS LAUNCHED
  /*@Bean
  @ConfigurationProperties(value = "discord-api")
  fun discordApi(): DiscordApi {
    val api = DiscordApiBuilder()
      .setToken(discordToken)
      .setWaitForServersOnStartup(false)
      .setAllNonPrivilegedIntents()
      .login()
      .join()

    CommandFactory(api).createCommands()

    val mapOfMusicPlayers = hashMapOf<Long, MusicPlayer>()

    api.addSlashCommandCreateListener { event ->
      val serverId = event.interaction.server.get().id
      mapOfMusicPlayers.getOrPut(serverId) { MusicPlayer(api) }.handleSlashCommand(event)

      if (event.slashCommandInteraction.commandName == CommandFactory.DISCONNECT) {
        mapOfMusicPlayers.remove(serverId)
      }
    }

    api.addMessageComponentCreateListener { event ->
      val serverId = event.interaction.server.get().id
      mapOfMusicPlayers.getOrPut(serverId) { MusicPlayer(api) }.handleMessageInteraction(event)
    }

    return api
  }*/
}