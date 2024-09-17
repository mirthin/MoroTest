package com.moro.moroTestKotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
class MoroTestKotlinApplication

fun main(args: Array<String>) {
	runApplication<MoroTestKotlinApplication>(*args)
}
