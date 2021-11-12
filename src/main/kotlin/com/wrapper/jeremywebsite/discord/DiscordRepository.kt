package com.wrapper.jeremywebsite.discord

import com.wrapper.jeremywebsite.GoogleCloudRepository
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class DiscordRepository {

  private val musicPlayer = MusicPlayer()

  @Bean
  @ConfigurationProperties(value = "discord-api")
  fun discordApi(): DiscordApi {
    val api = DiscordApiBuilder()
      .setToken(GoogleCloudRepository().accessSecretVersion())
      .setAllNonPrivilegedIntents()
      .login()
      .join()

    api.addMessageCreateListener {
      musicPlayer.handleMessage(it)
    }

    return api
  }
}