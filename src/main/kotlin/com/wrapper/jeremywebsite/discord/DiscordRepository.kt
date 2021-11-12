package com.wrapper.jeremywebsite.discord

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class DiscordRepository @Autowired constructor(val environment: Environment) {

  private val musicPlayer = MusicPlayer()

  @Bean
  @ConfigurationProperties(value = "discord-api")
  fun discordApi() {
    try {
      val api = DiscordApiBuilder()
        .setToken(environment.getProperty("discord.token"))
        .setWaitForServersOnStartup(false)
        .setAllNonPrivilegedIntents()
        .login()
        .join()

      api.addMessageCreateListener {
        musicPlayer.handleMessage(it)
      }
    } catch (e: Exception) {
      println(e)
    }
  }
}