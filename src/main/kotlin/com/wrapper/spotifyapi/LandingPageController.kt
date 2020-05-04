package com.wrapper.spotifyapi

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LandingPageController {

  @RequestMapping("/")
  fun home(): String = "Hello World"

}