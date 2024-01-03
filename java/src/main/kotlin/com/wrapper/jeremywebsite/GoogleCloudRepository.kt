package com.wrapper.jeremywebsite

import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1beta1.SecretVersionName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class GoogleCloudRepository @Autowired constructor(val environment: Environment) {

  @Throws(IOException::class)
  fun accessDiscordToken(): String = accessSecretVersion(
    environment.getProperty("google.cloud.id"),
    DISCORD_TOKEN,
    DISCORD_VERSION
  )

  @Throws(IOException::class)
  fun accessDevDiscordToken(): String = accessSecretVersion(
    environment.getProperty("google.cloud.id"),
    DISCORD_DEV_TOKEN,
    DISCORD_DEV_VERSION
  )

  @Throws(IOException::class)
  fun accessSpotifyToken(): String = accessSecretVersion(
    environment.getProperty("google.cloud.id"),
    SPOTIFY_TOKEN,
    SPOTIFY_VERSION
  )

  // Access the payload for the given secret version if one exists. The version
  // can be a version number as a string (e.g. "5") or an alias (e.g. "latest").
  @Throws(IOException::class)
  private fun accessSecretVersion(projectId: String?, secretId: String?, versionId: String?): String {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    SecretManagerServiceClient.create().use { client ->
      val secretVersionName = SecretVersionName.of(projectId, secretId, versionId)

      // Access the secret version.
      val response = client.accessSecretVersion(secretVersionName)

      // Print the secret payload.
      //
      // WARNING: Do not print the secret in a production environment - this
      // snippet is showing how to access the secret material.
      return response.payload.data.toStringUtf8()
    }
  }

  companion object {
    private const val DISCORD_TOKEN = "discord-token"
    private const val DISCORD_VERSION = "3"
    private const val DISCORD_DEV_TOKEN = "JeremyDevBot"
    private const val DISCORD_DEV_VERSION = "1"
    private const val SPOTIFY_TOKEN = "spotify-token"
    private const val SPOTIFY_VERSION = "1"
  }
}