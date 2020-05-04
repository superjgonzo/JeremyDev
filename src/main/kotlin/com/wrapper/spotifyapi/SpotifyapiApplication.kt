package com.wrapper.spotifyapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class SpotifyapiApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
	runApplication<SpotifyapiApplication>(*args)
}
