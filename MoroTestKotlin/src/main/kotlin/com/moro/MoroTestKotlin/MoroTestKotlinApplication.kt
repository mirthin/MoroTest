package com.moro.MoroTestKotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.moro.MoroTestKotlin"])
class MoroTestKotlinApplication

fun main(args: Array<String>) {
	runApplication<MoroTestKotlinApplication>(*args)
}
