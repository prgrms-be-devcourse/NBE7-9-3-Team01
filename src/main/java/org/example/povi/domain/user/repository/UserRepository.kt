package org.example.povi.domain.user.repository

import org.example.povi.auth.enums.AuthProvider
import org.example.povi.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}
