package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotifyapi.database.models.PartyRoom
import com.wrapper.spotifyapi.database.repository.PartyRoomRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.streams.asSequence

private const val SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
private const val LOBBY_ROOM_CODE_LENGTH = 5L

@Service
@RestController
@RequestMapping("/db")
class DatabaseController(private val partyRoomRepository: PartyRoomRepository) {

  fun createRoomNumber(): String = generateRoomCode()

  @GetMapping("/partyRooms")
  fun getAllRooms(): List<PartyRoom> {
    return partyRoomRepository.findAll()
  }

//  @PostMapping("/createRooms")
  fun createNewRoom(@Valid @RequestBody partyRoom: PartyRoom): PartyRoom {
    return partyRoomRepository.save(partyRoom)
  }

//  @GetMapping("/getRoom/{roomNumber}")
  fun getRoomsByRoomNumber(@PathVariable(value = "roomNumber") roomNumber: String): ResponseEntity<PartyRoom> {
    return partyRoomRepository.findById(roomNumber).map { room ->
      ResponseEntity.ok(room)
    }.orElse(ResponseEntity.notFound().build())
  }

//  @PutMapping("/putRoom/{roomNumber}")
  fun updateRoomByRoomNumber(
    @PathVariable(value = "roomNumber") roomNumber: String,
    @Valid @RequestBody newRoom: PartyRoom
  ) : ResponseEntity<PartyRoom> {
    return partyRoomRepository.findById(roomNumber).map { existingRoom ->
      val updatedRoom: PartyRoom = existingRoom
        .copy(
          roomNumber = newRoom.roomNumber,
          clientId = newRoom.clientId,
          playlistId = newRoom.playlistId,
          accessToken = newRoom.accessToken
        )
      ResponseEntity.ok().body(partyRoomRepository.save(updatedRoom))
    }.orElse(ResponseEntity.notFound().build())
  }

//  @DeleteMapping("/deleteRoom/{roomNumber}")
  fun deleteRoomByRoomNumber(@PathVariable(value = "roomNumber") roomNumber: String) : ResponseEntity<Void> {
    return partyRoomRepository.findById(roomNumber).map { room ->
      partyRoomRepository.delete(room)
      ResponseEntity<Void>(HttpStatus.OK)
    }.orElse(ResponseEntity.notFound().build())
  }

  private fun generateRoomCode(): String {
    return java.util.Random().ints(LOBBY_ROOM_CODE_LENGTH, 0, SOURCE.length)
      .asSequence()
      .map(SOURCE::get)
      .joinToString("")
  }
}