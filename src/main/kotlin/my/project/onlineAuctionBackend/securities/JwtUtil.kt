package my.project.onlineAuctionBackend.securities

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor("your-super-secret-key-your-super-secret-key".toByteArray())

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 ชั่วโมง
            .signWith(secretKey, Jwts.SIG.HS256) // ✅ เวอร์ชันใหม่ใช้ Jwts.SIG.HS256
            .compact()
    }

    fun extractUsername(token: String): String? {
        return try {
            Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: Exception) {
            println("Error extracting username from token: ${e.message}")
            null
        }
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
            !claims.payload.expiration.before(Date())
        } catch (e: ExpiredJwtException) {
            println("Token has expired: ${e.message}")
            false
        } catch (e: JwtException) {
            println("Invalid token: ${e.message}")
            false
        }
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username != null && username == userDetails.username && isTokenValid(token)
    }
}
