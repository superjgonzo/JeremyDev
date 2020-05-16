package com.wrapper.spotifyapi.endpoints

import com.wrapper.spotifyapi.database.models.PartyRoom
import com.wrapper.spotifyapi.database.repository.PartyRoomRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/db")
class TempDatabaseController(private val partyRoomRepository: PartyRoomRepository) {

  @GetMapping("/test")
  fun test(): String = "Testing this endpoint"

  @GetMapping("/partyRooms")
  fun getAllRooms(): List<PartyRoom> {
    println("get called")
    return partyRoomRepository.findAll()
  }

  @PostMapping("/partyRooms")
  fun createNewRoom(@Valid @RequestBody partyRoom: PartyRoom): PartyRoom {
    println("post called")
    return partyRoomRepository.save(partyRoom)
  }

  @GetMapping("/partyRooms/{roomNumber}")
  fun getRoomsByRoomNumber(@PathVariable(value = "roomNumber") roomNumber: Int): ResponseEntity<PartyRoom> {
    return partyRoomRepository.findById(roomNumber).map { room ->
      ResponseEntity.ok(room)
    }.orElse(ResponseEntity.notFound().build())
  }

  @PutMapping("/partyRooms/{roomNumber}")
  fun updateRoomByRoomNumber(
    @PathVariable(value = "roomNumber") roomNumber: Int,
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

  @DeleteMapping("/partyRooms/{roomNumber}")
  fun deleteRoombyRoomNumber(@PathVariable(value = "roomNumber") roomNumber: Int) : ResponseEntity<Void> {
    return partyRoomRepository.findById(roomNumber).map { room ->
      partyRoomRepository.delete(room)
      ResponseEntity<Void>(HttpStatus.OK)
    }.orElse(ResponseEntity.notFound().build())
  }

}