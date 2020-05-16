package com.wrapper.spotifyapi.database.repository

import com.wrapper.spotifyapi.database.models.PartyRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartyRoomRepository : JpaRepository<PartyRoom, Int>