package com.wrapper.jeremywebsite

import com.wrapper.jeremywebsite.spotifyapp.database.repository.SpotifyRepository
import com.wrapper.jeremywebsite.spotifyapp.endpoints.DatabaseController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

//private const val homePage = "http://localhost:8080"
private const val homePage = "http://superjgonzo.net"

@RestController
class LandingPageController @Autowired constructor(
  private val spotifyRepository: SpotifyRepository,
  private val databaseController: DatabaseController
) {

  @RequestMapping("/")
  fun home(): String = "Hello World this is Jeremy!!"

  @RequestMapping("/welcome")
  fun loggedIn(): String = "WELCOME!"

  @RequestMapping("/roomClosed")
  fun roomClosed(): String = "Room Closed!"

  @RequestMapping(value = ["/login"], method = [RequestMethod.GET])
  fun login(): ModelAndView? {
    val authorizationResult = spotifyRepository.authorizationCodeURI()
    return ModelAndView("redirect:$authorizationResult")
  }

  @RequestMapping("/callback")
  fun callback(@RequestParam code: String): ModelAndView? {
    spotifyRepository.authorizationCode(code)
    return ModelAndView("redirect:$homePage")
  }

  @RequestMapping("/joinRoom")
  fun joinRoom(@RequestParam roomNumber: String) : ModelAndView? {
    spotifyRepository.guestAccessCode(roomNumber)
    return ModelAndView("redirect:$homePage/welcome")
  }

  @RequestMapping("/closeRoom")
  fun closeRoom(@RequestParam roomNumber: String) : ModelAndView? {
    databaseController.deleteRoomByRoomNumber(roomNumber)
    return ModelAndView("redirect:$homePage/roomClosed")
  }
}