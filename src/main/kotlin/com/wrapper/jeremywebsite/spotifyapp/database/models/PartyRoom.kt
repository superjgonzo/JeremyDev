package com.wrapper.jeremywebsite.spotifyapp.database.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank

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
  val accessToken: String = " ",

  @get: NotBlank
  val refreshToken: String = " "

)