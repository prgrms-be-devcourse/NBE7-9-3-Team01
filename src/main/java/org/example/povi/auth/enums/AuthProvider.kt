package org.example.povi.auth.enums

enum class AuthProvider(val providerName: String) {
    LOCAL("local"),
    KAKAO("kakao"),
    GOOGLE("google");

    companion object {
        fun from(provider: String?): AuthProvider =
            entries.find { it.providerName.equals(provider, ignoreCase = true) }
                ?: LOCAL
    }
}