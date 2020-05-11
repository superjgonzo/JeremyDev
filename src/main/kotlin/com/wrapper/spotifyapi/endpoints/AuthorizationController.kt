package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class AuthorizationController(private val spotifyApi: SpotifyApi) {

  private val authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
    .scope("user-read-private user-read-email playlist-modify-public playlist-modify-private playlist-read-private user-read-currently-playing")
    .show_dialog(true)
    .build()

  fun authorizationCodeURIAsync(): String {
    return try {
      val uriFuture = authorizationCodeUriRequest.executeAsync()
      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      return uriFuture.join().toString()

    } catch (e: CompletionException) {
      "Error: " + e.cause?.message + " FROM AUTHORIZATION CODE URI ASYNC"
    } catch (e: CancellationException) {
      "Async operation cancelled."
    }
  }

  fun authorizationCodeAsync(code: String) {
    val authorizationCodeRequest = spotifyApi.authorizationCode(code).build()

    try {
      val authorizationCodeCredentialsFuture: CompletableFuture<AuthorizationCodeCredentials> = authorizationCodeRequest.executeAsync()

      // Example Only. Never block in production code.
      val authorizationCodeCredentials = authorizationCodeCredentialsFuture.join()

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.accessToken = authorizationCodeCredentials.accessToken
      spotifyApi.refreshToken = authorizationCodeCredentials.refreshToken

    } catch (e: CompletionException) {
      println("Error: " + e.cause!!.message + " FROM AUTHORIZATION CODE ASYNC")
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
    }
  }

}