package com.wrapper.jeremywebsite.discordbot

import com.wrapper.jeremywebsite.GoogleCloudRepository
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class DiscordRepository @Autowired constructor(googleCloudRepository: GoogleCloudRepository) {

  private val discordToken = googleCloudRepository.accessDevDiscordToken()

// uncomment an put discord token
//  private val discordToken = ""

 /* @Bean
  @ConfigurationProperties(value = "discord-api")
  fun discordApi(): DiscordApi {
    return DiscordApiBuilder()
      .setToken(discordToken)
      .setWaitForServersOnStartup(false)
      .setAllNonPrivilegedIntents()
      .login()
      .join()
  }*/
}