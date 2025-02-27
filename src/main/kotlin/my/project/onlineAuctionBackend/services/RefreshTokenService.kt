package my.project.onlineAuctionBackend.services

import my.project.onlineAuctionBackend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(private val jwtService: JwtService, private val userRepository: UserRepository) {

    fun refreshToken(refreshToken: String): String {
        val username = jwtService.extractUsername(refreshToken)
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        if (!jwtService.validateToken(refreshToken)) {
            throw RuntimeException("Invalid Refresh Token")
        }

        return jwtService.generateToken(username)
    }
}