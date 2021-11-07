package com.wrapper.jeremywebsite.spotifyapp.database.repository

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest
import com.wrapper.jeremywebsite.spotifyapp.database.models.PartyRoom
import com.wrapper.jeremywebsite.spotifyapp.endpoints.DatabaseController
import org.springframework.stereotype.Service
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletionException


private const val clientId = "00e493dfeeb14ff98a17caeacc82c244"
private const val clientSecret = "af87e6b4f2b142a9bee7f2c6761dbca0"
//private val redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/callback")
private val redirectUri = SpotifyHttpManager.makeUri("http://superjgonzo.net/callback")
private const val PLAYLIST_NAME = "PartyQueue"

@Service
class SpotifyRepository(private val databaseController: DatabaseController) {

  private val spotifyApi =
    SpotifyApi.Builder()
      .setClientId(clientId)
      .setClientSecret(clientSecret)
      .setRedirectUri(redirectUri)
      .build()

  private var playlistId = ""
  private var roomNumber = ""

  fun spotifyRepository() = SpotifyRepositoryData(spotifyApi, playlistId)

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

      // re-enable when you want to take on the costs of google database fees....
//      roomNumber = databaseController.createRoomNumber()
//
//      databaseController.createNewRoom(
//        PartyRoom(
//          roomNumber = roomNumber,
//          clientId = clientId,
//          playlistId = getPartyQueuePlaylist(),
//          accessToken = authorizationCodeCredentials.accessToken,
//          refreshToken = authorizationCodeCredentials.refreshToken
//        )
//      )

    } catch (e: CompletionException) {
      println("Error: " + e.message)
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
    }
  }

  fun authorizationCodeRefresh(): Boolean {
    val authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
      .build()

    println("Before Refresh Token: ${spotifyApi.refreshToken}")

    /**
     * blocking call to reset access token as we cannot do anything until access token is refreshed
     */
    return try {
      val authorizationCodeCredentials = authorizationCodeRefreshRequest.execute()

      println("Before Access Token: ${spotifyApi.accessToken}")
      println("Before Refresh Token: ${spotifyApi.refreshToken}")

      // Set access and refresh token for further "spotifyApi" object usage
      spotifyApi.accessToken = authorizationCodeCredentials.accessToken
      spotifyApi.refreshToken = authorizationCodeCredentials.refreshToken

      println("\nAfter Access Token: ${spotifyApi.accessToken}")
      println("After Refresh Token: ${spotifyApi.refreshToken}")

      databaseController.updateRoomByRoomNumber(
        roomNumber = roomNumber,
        newRoom = PartyRoom(
          roomNumber = roomNumber,
          clientId = clientId,
          playlistId = playlistId,
          accessToken = authorizationCodeCredentials.accessToken,
          refreshToken = authorizationCodeCredentials.refreshToken
        )
      )

      true
    } catch (e: IOException) {
      println("Error 1: " + e.message)
      false
    } catch (e: SpotifyWebApiException) {
      println("Error BOI: " + e.message)
      false
    } catch (e: Exception) {
      println("Error UH OH: " + e.message)
      false
    }
  }

  fun guestAccessCode(roomNumber: String) {
    val partyRoom = databaseController.getRoomsByRoomNumber(roomNumber)

    spotifyApi.accessToken = partyRoom.body?.accessToken
    playlistId = partyRoom.body?.playlistId ?: ""
  }

  private fun getPartyQueuePlaylist(): String {
    val getListOfCurrentUsersPlaylistsRequest = spotifyApi.listOfCurrentUsersPlaylists
      .build()

    return try {
      val pagingFuture = getListOfCurrentUsersPlaylistsRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val playlistSimplifiedPaging = pagingFuture.join()

      return if (playlistSimplifiedPaging.items.any { it.name == PLAYLIST_NAME }) {
        playlistSimplifiedPaging.items.find{ it.name == PLAYLIST_NAME }?.id!!.also {
          playlistId = it
        }
      } else {
        createPlaylist()
      }

    } catch (e: CompletionException) {
      "Error: " + e.cause?.message
    } catch (e: CancellationException) {
      "Async operation cancelled."
    }
  }

  private fun createPlaylist(): String {
    val createPlaylistRequest = spotifyApi.createPlaylist(getUserId(), PLAYLIST_NAME)
      .build()

    return try {
      val playlistFuture = createPlaylistRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val playlist = playlistFuture.join()
      playlist.id.also {
        playlistId = it
      }
    } catch (e: CompletionException) {
      println("Error: " + e.cause!!.message)
      ""
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
      ""
    }

  }

  private fun getUserId(): String {
    val getCurrentUsersProfileRequest: GetCurrentUsersProfileRequest = spotifyApi.currentUsersProfile
      .build()

    return try {
      val userFuture = getCurrentUsersProfileRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val user = userFuture.join()
      user.id
    } catch (e: CompletionException) {
      println("Error: " + e.cause?.message)
      "ERROR: " + e.cause?.message
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
      "ERROR: " + e.cause?.message
    }
  }
}

data class SpotifyRepositoryData(
  val spotifyApi: SpotifyApi,
  val playlistId: String
)