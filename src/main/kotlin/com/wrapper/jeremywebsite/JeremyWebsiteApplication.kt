package com.wrapper.jeremywebsite

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class JeremyWebsiteApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
	runApplication<JeremyWebsiteApplication>(*args)
}
