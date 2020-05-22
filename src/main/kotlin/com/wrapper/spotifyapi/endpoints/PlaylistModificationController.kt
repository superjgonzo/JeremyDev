package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotify.model_objects.special.SnapshotResult
import com.wrapper.spotifyapi.database.repository.SpotifyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

@RestController
@RequestMapping("/api/playlist")
class PlaylistModificationController @Autowired constructor(
  private val spotifyRepository: SpotifyRepository
) {

  private val tempUris = arrayOf("spotify:track:01iyCAUm8EvOFqVWYJ3dVX", "spotify:episode:4GI3dxEafwap1sFiTGPKd1")

  @RequestMapping("/addSong")
  fun addSongToPlaylist(@RequestParam songs: Array<String>) { addItemsToPlaylistAsync(songs) }

  private fun addItemsToPlaylistAsync(songs: Array<String>) {
    val addItemsToPlaylistRequest = spotifyRepository
      .spotifyRepository()
      .spotifyApi
      .addItemsToPlaylist(
        spotifyRepository.spotifyRepository().playlistId,
        songs
      )
      .build()

    try {
      val snapshotResultFuture: CompletableFuture<SnapshotResult> = addItemsToPlaylistRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val snapshotResult: SnapshotResult = snapshotResultFuture.join()
      println("Snapshot ID: " + snapshotResult.snapshotId)
    } catch (e: CompletionException) {
      println("Error: " + e.cause?.message)
    } catch (e: CancellationException) {
      println("Async operation cancelled.")
    }
  }

}