package com.wrapper.spotifyapi

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

  @RequestMapping("/message")
  fun message(): Message = Message("Hello World whilst using KOTLIN", "High")
}

data class Message(
  val text: String,
  val priority: String
)