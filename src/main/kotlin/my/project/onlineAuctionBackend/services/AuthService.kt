package my.project.onlineAuctionBackend.services

import my.project.onlineAuctionBackend.models.User
import my.project.onlineAuctionBackend.repositories.UserRepository
import my.project.onlineAuctionBackend.securities.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    fun register(user: User): String {
        if (userRepository.existsByUsername(user.username)) {
            throw RuntimeException("Username is already taken")
        }

        val hashedPassword = passwordEncoder.encode(user.password)
        val newUser = user.copy(password = hashedPassword)
        userRepository.save(newUser)

        return "User registered successfully"
    }

    // old
//    fun login(username: String, password: String): String {
//        val user = userRepository.findByUsername(username)
//            ?: throw RuntimeException("Invalid username or password")
//
//        if (!passwordEncoder.matches(password, user.password)) {
//            throw RuntimeException("Invalid username or password")
//        }
//
//        return jwtUtil.generateToken(user.username)
//    }

    // new
    fun login(username: String, password: String): String? {
        val user = userRepository.findByUsername(username)
            ?: throw RuntimeException("User not found") // 🛑 ถ้าไม่เจอ ให้ส่ง 401 Unauthorized

        if (!passwordEncoder.matches(password, user.password)) {
            throw RuntimeException("Invalid password") // 🛑 ถ้า Password ไม่ตรงกัน ให้ส่ง 401
        }

        return jwtUtil.generateToken(user.username) // ✅ ต้องแน่ใจว่า Token ถูกสร้าง
    }

}
