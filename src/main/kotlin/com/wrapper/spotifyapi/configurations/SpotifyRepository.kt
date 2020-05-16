package com.wrapper.spotifyapi.configurations

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import org.springframework.stereotype.Service
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletionException

private const val clientId = "00e493dfeeb14ff98a17caeacc82c244"
private const val clientSecret = "af87e6b4f2b142a9bee7f2c6761dbca0"
private val localRedirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/callback")
private val redirectUri = SpotifyHttpManager.makeUri("http://superjgonzo.net/callback")

@Service
class SpotifyRepository {

  private val spotifyApi =
    SpotifyApi.Builder()
      .setClientId(clientId)
      .setClientSecret(clientSecret)
      .setRedirectUri(redirectUri)
      .build()

  fun spotifyApi(): SpotifyApi = spotifyApi

  private val authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
    .scope("user-read-private user-read-email playlist-modify-public playlist-modify-private playlist-read-private user-read-currently-playing")
    .build()

  fun authorizationCodeURI(): String {
    return try {
      val uriFuture = authorizationCodeUriRequest.executeAsync()
      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      return uriFuture.join().toString()

    } catch (e: CompletionException) {
      "Error: " + e.message
    } catch (e: CancellationException) {
      "Async operation cancelled."
    }
  }

  fun authorizationCode(code: String) {
    val authorizationCodeRequest = spotifyApi.authorizationCode(code).build()

    try {
      val authorizationCodeCredentialsFuture = authorizationCodeRequest.executeAsync()

      // Example Only. Never block in production code.
      val authorizationCodeCredentials = authorizationCodeCredentialsFuture.join()

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.accessToken = authorizationCodeCredentials.accessToken
      spotifyApi.refreshToken = authorizationCodeCredentials.refreshToken

    } catch (e: CompletionException) {
      println("Error: " + e.message)
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
    }
  }

  fun authorizationCodeRefresh() {
    val authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
      .build()

    try {
      val authorizationCodeCredentialsFuture = authorizationCodeRefreshRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val authorizationCodeCredentials = authorizationCodeCredentialsFuture.join()

      // Set access token for further "spotifyApi" object usage
      spotifyApi.accessToken = authorizationCodeCredentials.accessToken
    } catch (e: CompletionException) {
      println("Error: " + e.cause!!.message)
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
    }
  }

}