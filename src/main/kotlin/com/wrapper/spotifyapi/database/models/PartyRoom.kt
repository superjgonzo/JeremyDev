package com.wrapper.spotifyapi.database.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
data class PartyRoom(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val roomNumber: Int = 0,

  @get: NotBlank
  val clientId: String = " ",

  @get: NotBlank
  val playlistId: String = " ",

  @get: NotBlank
  val accessToken: String = " "

)