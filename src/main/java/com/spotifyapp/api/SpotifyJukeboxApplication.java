package com.spotifyapp.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpotifyJukeboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyJukeboxApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "Hello World!";
	}

	@GetMapping("/seiko")
	public String seiko() {
		return "Seiko Hiromi Hosoki (in a miami accent) IS THE BEST";
	}
}
