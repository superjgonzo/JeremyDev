package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotify.enums.ModelObjectType
import com.wrapper.spotify.model_objects.specification.Image
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import com.wrapper.spotifyapi.database.repository.SpotifyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

private const val spotifyTrackPrefix = "spotify:track:"
private const val spotifyEpisodePrefix = "spotify:episode:"

@RestController
@RequestMapping("/api")
class SongController @Autowired constructor(
  private val spotifyRepository: SpotifyRepository
) {

  @RequestMapping("/searchSongs")
  fun searchForSong(@RequestParam song: String): List<Song> = searchSongAsync(song)

  private fun searchSongAsync(searchQuery: String): List<Song> {
    val searchSongRequest = spotifyRepository.spotifyRepository().spotifyApi.searchTracks(searchQuery)
      .build()

    return try {
      val pagingFuture: CompletableFuture<Paging<Track>> = searchSongRequest.executeAsync()

      val trackItems = pagingFuture.join()
      val listOfSongs = mutableListOf<Song>()

      trackItems.items.forEach {
        listOfSongs.add(
          Song(
            it.name,
            it.artists[0].name,
            it.album.images[0],
            getTrackTypePrefix(it.type) + it.id
          )
        )
      }
      listOfSongs
    } catch (e: CompletionException) {
      println("Error Type: " + e.cause?.message)
      listOf(Song("Error: " + e.cause?.message, "", null, ""))
    } catch (e: CancellationException) {
      println("Error Type: " + e.cause?.message)
      listOf(Song("Error: " + e.cause?.message, "", null, ""))
    }
  }

  private fun getTrackTypePrefix(trackType: ModelObjectType): String {
    return when(trackType) {
      ModelObjectType.TRACK -> {
        spotifyTrackPrefix
      }
      ModelObjectType.EPISODE -> {
        spotifyEpisodePrefix
      }
      else -> ""
    }
  }
}

data class Song(
  val songTitle: String,
  val artist: String,
  val albumArt: Image?,
  val songId: String
)