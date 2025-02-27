package my.project.onlineAuctionBackend.services

import my.project.onlineAuctionBackend.securities.JwtUtil
import org.springframework.stereotype.Service

@Service
class JwtService(private val jwtUtil: JwtUtil) {

    fun extractUsername(token: String): String {
        return jwtUtil.extractUsername(token)
    }

    fun validateToken(token: String): Boolean {
        return jwtUtil.validateToken(token)
    }

    fun generateToken(username: String): String {
        return jwtUtil.generateToken(username)
    }
}
