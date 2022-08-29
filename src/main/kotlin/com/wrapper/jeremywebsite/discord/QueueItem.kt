package com.wrapper.jeremywebsite.discord

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.javacord.api.entity.user.User

data class QueueItem(
  val track: AudioTrack,
  val queuedBy: User
)