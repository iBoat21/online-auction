package my.project.onlineAuctionBackend.controllers

import my.project.onlineAuctionBackend.services.AuthService
import my.project.onlineAuctionBackend.services.RefreshTokenService
import my.project.onlineAuctionBackend.dto.LoginRequest
import my.project.onlineAuctionBackend.dto.RegisterRequest
import my.project.onlineAuctionBackend.dto.RefreshTokenRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import my.project.onlineAuctionBackend.models.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            val user = User(
                username = request.username,
                email = request.email,
                password = request.password
            )
            val message = authService.register(user)
            ResponseEntity.ok(mapOf("message" to message))
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
        }
    }

    // old
//    @PostMapping("/login")
//    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
//        val (username, password) = loginRequest
//        return try {
//            val token = authService.login(username, password)
//            ResponseEntity.ok(mapOf("token" to token))
//        } catch (e: RuntimeException) {
//            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to e.message))
//        }
//    }

    // new
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        val (username, password) = loginRequest
        return try {
            val token = authService.login(username, password) // 🛑 เช็คว่าคืนค่า null หรือไม่
            if (token.isNullOrBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Invalid credentials"))
            }
            ResponseEntity.ok(mapOf("token" to token)) // ✅ ต้องแน่ใจว่า token ถูกคืนไปยัง React
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to e.message))
        }
    }


    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ResponseEntity<Any> {
        return try {
            val newAccessToken = refreshTokenService.refreshToken(request.refreshToken)
            ResponseEntity.ok(mapOf("token" to newAccessToken))
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to e.message))
        }
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal user: UserDetails?): ResponseEntity<Map<String, Any>> {
        return if (user != null) {
            ResponseEntity.ok(
                mapOf(
                    "username" to user.username
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                mapOf("message" to "Unauthorized")
            )
        }
    }

}


