package com.wrapper.jeremywebsite.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import org.javacord.api.DiscordApi
import org.javacord.api.audio.AudioSource
import org.javacord.api.audio.AudioSourceBase


/**
 * Creates a new lavaplayer audio source.
 *
 * @param api A discord api instance.
 * @param audioPlayer An audio player from Lavaplayer.
 */
class LavaPlayerAudioSource(api: DiscordApi?, private val audioPlayer: AudioPlayer) : AudioSourceBase(api) {

  private var lastFrame: AudioFrame? = null

  override fun getNextFrame(): ByteArray? {
    return lastFrame?.let { applyTransformers(it.data) }
  }

  override fun hasFinished(): Boolean = false

  override fun hasNextFrame(): Boolean {
    lastFrame = audioPlayer.provide()
    return lastFrame != null
  }

  override fun copy(): AudioSource = LavaPlayerAudioSource(api, audioPlayer)
}