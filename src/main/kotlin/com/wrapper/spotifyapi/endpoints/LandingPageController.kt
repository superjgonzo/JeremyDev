package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotifyapi.configurations.SpotifyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

private const val homePage = "http://localhost:8080"
//private const val homePage = "http://superjgonzo.net"

@RestController
class LandingPageController @Autowired constructor(
  private val spotifyRepository: SpotifyRepository
) {

  @RequestMapping("/")
  fun home(): String = "Hello World"

  @RequestMapping(value = ["/login"], method = [RequestMethod.GET])
  fun login(): ModelAndView?{
    val authorizationResult = spotifyRepository.authorizationCodeURI()
    return ModelAndView("redirect:$authorizationResult")
  }

  @RequestMapping("/callback")
  fun callback(@RequestParam code: String): ModelAndView? {
    spotifyRepository.authorizationCode(code)
    return ModelAndView("redirect:$homePage")
  }

}