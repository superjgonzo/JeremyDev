package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.specification.AlbumSimplified
import com.wrapper.spotify.model_objects.specification.Paging
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException


private const val clientId = "00e493dfeeb14ff98a17caeacc82c244"
private const val clientSecret = "af87e6b4f2b142a9bee7f2c6761dbca0"
private val localRedirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/callback")
private val redirectUri = SpotifyHttpManager.makeUri("http://superjgonzo.net/callback")

@RestController
class MessageController {

  private val id = "0sNOF9WDwhWunNAHPD3Baj"

  private val spotifyApi = SpotifyApi.Builder()
    .setClientId(clientId)
    .setClientSecret(clientSecret)
    .setRedirectUri(redirectUri)
    .build()

  private val authorizationController = AuthorizationController(spotifyApi)

  @RequestMapping("/message")
  fun message(): Message = Message("Hello World whilst using KOTLIN")

  @RequestMapping("/temp2")
  fun searchForAlbums(@RequestParam albumSearch: String): List<Message> = searchAlbumsAsync(albumSearch)

  @RequestMapping("/callback")
  fun callback(@RequestParam code: String): List<Message> {
    authorizationController.authorizationCodeAsync(code)
    return searchForAlbums("Madeon")
  }

  @RequestMapping(value = ["/login"], method = [RequestMethod.GET])
  fun login(): ModelAndView?{
    val authorizationResult = AuthorizationController(spotifyApi).authorizationCodeURIAsync()
    return ModelAndView("redirect:$authorizationResult")
  }

  fun searchAlbumsAsync(searchQuery: String): List<Message> {
    val searchAlbumsRequest = spotifyApi.searchAlbums(searchQuery)
      .build()

    return try {
      val pagingFuture: CompletableFuture<Paging<AlbumSimplified>> = searchAlbumsRequest.executeAsync()

      // Thread free to do other tasks...

      // Example Only. Never block in production code.
      val albumSimplifiedPaging = pagingFuture.join()
      val listOfAlbums = mutableListOf<Message>()

      albumSimplifiedPaging.items.forEach {
        println("album title: " + it.name)
        listOfAlbums.add(Message(
          albumTitle = it.name
        ))
      }

      listOfAlbums
    } catch (e: CompletionException) {
      println("Error: " + e.cause!!.message)
      listOf(Message(albumTitle = "Error: " + e.cause!!.message))
    } catch (e: CancellationException) {
      listOf(Message(albumTitle = "Async operation cancelled."))
    }
  }
}

data class Message(
  val albumTitle: String
)