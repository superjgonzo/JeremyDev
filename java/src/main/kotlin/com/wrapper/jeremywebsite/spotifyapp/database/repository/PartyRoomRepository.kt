package com.wrapper.jeremywebsite.spotifyapp.database.repository

import com.wrapper.jeremywebsite.spotifyapp.database.models.PartyRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRoomRepository : JpaRepository<PartyRoom, String>