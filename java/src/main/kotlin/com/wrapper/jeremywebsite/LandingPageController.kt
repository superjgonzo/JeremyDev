package com.wrapper.jeremywebsite

import com.wrapper.jeremywebsite.spotifyapp.database.repository.SpotifyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

private const val WEB_URL = "website.url"

@RestController
class LandingPageController @Autowired constructor(
  private val spotifyRepository: SpotifyRepository,
  val environment: Environment
) {

  @RequestMapping("/")
  fun home(): String = "Hello World this is Jeremy!!\n Testing some stuff"

  @RequestMapping("/welcome")
  fun loggedIn(): String = "Testing some memes"

  @RequestMapping("/roomClosed")
  fun roomClosed(): String = "Room Closed!"

  @RequestMapping(value = ["/login"], method = [RequestMethod.GET])
  fun login(): ModelAndView? {
    val authorizationResult = spotifyRepository.authorizationCodeURI()
    return ModelAndView("redirect:$authorizationResult")
  }

  @RequestMapping("/callback")
  fun callback(@RequestParam code: String): ModelAndView {
    spotifyRepository.authorizationCode(code)
    val homePage = environment.getProperty(WEB_URL)
    return ModelAndView("redirect:$homePage")
  }
}