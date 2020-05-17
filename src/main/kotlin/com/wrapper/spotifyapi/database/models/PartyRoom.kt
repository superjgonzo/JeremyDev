package com.wrapper.spotifyapi.database.models

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class PartyRoom(
  @Id
  @Column(name = "room_number", nullable = false)
  val roomNumber: String = " ",

  @get: NotBlank
  val clientId: String = " ",

  @get: NotBlank
  val playlistId: String = " ",

  @get: NotBlank
  val accessToken: String = " "

)