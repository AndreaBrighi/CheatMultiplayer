package org.example.server.interfaceAdaptersLayer.controllers

import org.example.server.businessLayer.boundaries.UserInputBoundary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class LoginController(
    val userInputBoundary: UserInputBoundary,
) {
    @GetMapping("/greeting")
    fun greeting(
        @RequestParam(value = "name", defaultValue = "World") name: String?,
    ): HttpEntity<String> {
        val greeting = "Hello, $name!"

        return ResponseEntity(greeting, HttpStatus.OK)
    }
}
