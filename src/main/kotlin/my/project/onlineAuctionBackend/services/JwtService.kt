package my.project.onlineAuctionBackend.services

import my.project.onlineAuctionBackend.securities.JwtUtil
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UserDetails

@Service
class JwtService(private val jwtUtil: JwtUtil) {

    fun extractUsername(token: String): String {
        return jwtUtil.extractUsername(token) ?: throw Exception("Invalid Token")
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        return jwtUtil.validateToken(token, userDetails)
    }

    fun generateToken(username: String): String {
        return jwtUtil.generateToken(username)
    }
}
