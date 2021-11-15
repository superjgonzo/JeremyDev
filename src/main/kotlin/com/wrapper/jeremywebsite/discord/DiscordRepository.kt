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

  private val musicPlayer = MusicPlayer()
  private val discordToken = googleCloudRepository.accessDiscordToken()

  @Bean
  @ConfigurationProperties(value = "discord-api")
  fun discordApi(): DiscordApi {
    val api = DiscordApiBuilder()
      .setToken(discordToken)
      .setAllNonPrivilegedIntents()
      .login()
      .join()

    api.addMessageCreateListener { event ->
      if (!event.messageAuthor.isYourself) {
        musicPlayer.handleMessage(event)
      }
    }

    return api
  }
}