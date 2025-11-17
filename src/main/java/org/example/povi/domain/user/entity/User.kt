package org.example.povi.domain.user.entity

import jakarta.persistence.*
import org.example.povi.auth.enums.AuthProvider
import org.example.povi.global.entity.BaseEntity

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true, length = 100)
    var email: String,

    @Column(nullable = false)
    var password: String? = null,

    @Column(nullable = false, length = 50)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: AuthProvider,

    var providerId: String? = null,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    var userRole: UserRole = UserRole.USER,

    @Column(nullable = false)
    var isEmailVerified: Boolean = false,

    ) : BaseEntity() {

    // JPA 기본 생성자
    protected constructor() : this(
        email = "",
        password = null,
        nickname = "",
        provider = AuthProvider.LOCAL
    )

    companion object {

        fun builder() = Builder()

        class Builder {
            private var email: String = ""
            private var password: String? = null
            private var nickname: String = ""
            private var provider: AuthProvider = AuthProvider.LOCAL
            private var providerId: String? = null
            private var bio: String? = null
            private var userRole: UserRole = UserRole.USER
            private var isEmailVerified: Boolean = false

            fun email(value: String) = apply { this.email = value }
            fun password(value: String?) = apply { this.password = value }
            fun nickname(value: String) = apply { this.nickname = value }
            fun provider(value: AuthProvider) = apply { this.provider = value }
            fun providerId(value: String?) = apply { this.providerId = value }
            fun bio(value: String?) = apply { this.bio = value }
            fun userRole(value: UserRole) = apply { this.userRole = value }
            fun isEmailVerified(value: Boolean) = apply { this.isEmailVerified = value }

            fun build(): User =
                User(
                    email = email,
                    password = password,
                    nickname = nickname,
                    provider = provider,
                    providerId = providerId,
                    bio = bio,
                    userRole = userRole,
                    isEmailVerified = isEmailVerified
                )
        }
    }

    fun verifyEmail() {
        this.isEmailVerified = true
    }

    fun updateProfileImgUrl(url: String?) {
        this.bio = url
    }

    fun updateNickname(nick: String?) {
        if (nick != null) this.nickname = nick
    }

    fun updateBio(newBio: String?) {
        this.bio = newBio
    }
}