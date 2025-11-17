package org.example.povi.auth.controller

import jakarta.validation.Valid
import org.example.povi.auth.controller.docs.AuthControllerDocs
import org.example.povi.auth.dto.*
import org.example.povi.auth.dto.MeResponseDto.Companion.from
import org.example.povi.auth.service.AuthService
import org.example.povi.auth.token.service.TokenService
import org.example.povi.auth.util.SecurityUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val tokenService: TokenService
) : AuthControllerDocs {

    @PostMapping("/signup")
    override fun signup(@Valid @RequestBody requestDto: SignupRequestDto): ResponseEntity<Void> {
        authService.signup(requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/login")
    override fun login(@RequestBody requestDto: LoginRequestDto): ResponseEntity<LoginResponseDto> {
        return ResponseEntity.ok(authService.login(requestDto))
    }

    @GetMapping("/me")
    override fun myInfo(): ResponseEntity<MeResponseDto> {
        val user = SecurityUtil.currentUserOrThrow
        return ResponseEntity.ok(from(user))
    }

    @PostMapping("/token/reissue")
    override fun reissueAccessToken(
        @Valid @RequestBody requestDto: TokenReissueRequestDto
    ): ResponseEntity<TokenReissueResponseDto> {
        return ResponseEntity.ok(tokenService.reissueAccessToken(requestDto))
    }

    @PostMapping("/logout")
    override fun logout(): ResponseEntity<Void> {
        val user = SecurityUtil.currentUserOrThrow
        authService.logout(user.id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/oauth/callback/{provider}")
    override fun oauthCallback(
        @PathVariable provider: String,
        @RequestParam("accessToken") accessToken: String,
        @RequestParam("refreshToken") refreshToken: String
    ): ResponseEntity<TokenReissueResponseDto> {
        return ResponseEntity.ok(TokenReissueResponseDto(accessToken, refreshToken))
    }

    @DeleteMapping("/withdraw")
    override fun withdraw(): ResponseEntity<Void> {
        val user = SecurityUtil.currentUserOrThrow
        authService.withdraw(user.id)
        return ResponseEntity.noContent().build()
    }
}