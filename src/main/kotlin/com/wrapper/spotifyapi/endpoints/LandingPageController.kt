package com.wrapper.spotifyapi.endpoints

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LandingPageController {

  @RequestMapping("/")
  fun home(): String = "Hello World"

}