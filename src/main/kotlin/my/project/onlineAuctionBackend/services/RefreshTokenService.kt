package my.project.onlineAuctionBackend.services

import my.project.onlineAuctionBackend.repositories.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(private val jwtService: JwtService, private val userRepository: UserRepository) {

    fun refreshToken(refreshToken: String): String {
        val username = jwtService.extractUsername(refreshToken)
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        // ✅ โหลด UserDetails จากฐานข้อมูล
        val userDetails: UserDetails = User(user.username, user.password, listOf())

        // ✅ ส่ง userDetails ไปที่ validateToken()
        if (!jwtService.validateToken(refreshToken, userDetails)) {
            throw RuntimeException("Invalid Refresh Token")
        }

        return jwtService.generateToken(username)
    }
}