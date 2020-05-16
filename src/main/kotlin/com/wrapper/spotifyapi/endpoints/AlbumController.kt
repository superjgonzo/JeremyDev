package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotify.model_objects.specification.AlbumSimplified
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotifyapi.configurations.SpotifyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

@RestController
class AlbumController @Autowired constructor(
  private val spotifyRepository: SpotifyRepository
) {

  @RequestMapping("/getAlbums")
  fun searchForAlbums(@RequestParam artist: String): List<Album> = searchAlbumsAsync(artist)

  private fun searchAlbumsAsync(searchQuery: String): List<Album> {
    val searchAlbumsRequest = spotifyRepository.spotifyApi().searchAlbums(searchQuery)
      .build()

    println("refresh token: " + spotifyRepository.spotifyApi().refreshToken)

    return try {
      val pagingFuture: CompletableFuture<Paging<AlbumSimplified>> = searchAlbumsRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val albumSimplifiedPaging = pagingFuture.join()
      val listOfAlbums = mutableListOf<Album>()

      albumSimplifiedPaging.items.forEach {
        listOfAlbums.add(Album(
          albumTitle = it.name
        ))
      }

      listOfAlbums
    } catch (e: CompletionException) {
      println("Error Type: " + e.cause!!.toString())
      println("Error: " + e.cause!!.message)
      listOf(Album(albumTitle = "Error: " + e.cause!!.message))
    } catch (e: CancellationException) {
      listOf(Album(albumTitle = "Async operation cancelled."))
    }
  }
}

data class Album(
  val albumTitle: String
)