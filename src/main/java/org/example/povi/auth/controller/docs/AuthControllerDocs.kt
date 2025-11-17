package org.example.povi.auth.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.povi.auth.dto.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰 재발급, 로그아웃 등 인증 기능 제공")
interface AuthControllerDocs {

    // 회원가입
    @Operation(summary = "회원가입", description = "이메일 기반 회원가입을 수행합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    fun signup(
        @RequestBody requestDto: SignupRequestDto
    ): ResponseEntity<Void>

    // 로그인
    @Operation(summary = "로그인", description = "이메일/비밀번호 로그인 후 토큰 발급")
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
        content = [Content(schema = Schema(implementation = LoginResponseDto::class))]
    )
    fun login(
        @RequestBody requestDto: LoginRequestDto
    ): ResponseEntity<LoginResponseDto>

    // 내 정보 조회
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자 정보 조회")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = MeResponseDto::class))]
    )
    fun myInfo(): ResponseEntity<MeResponseDto>

    // 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 Access Token 재발급")
    @ApiResponse(
        responseCode = "200",
        description = "토큰 재발급 성공",
        content = [Content(schema = Schema(implementation = TokenReissueResponseDto::class))]
    )
    fun reissueAccessToken(
        @RequestBody requestDto: TokenReissueRequestDto
    ): ResponseEntity<TokenReissueResponseDto>

    // 로그아웃
    @Operation(summary = "로그아웃", description = "Refresh Token 삭제하여 로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    fun logout(): ResponseEntity<Void>

    // OAuth 콜백
    @Operation(summary = "OAuth2 콜백", description = "구글/카카오 소셜 로그인 완료 후 Access/Refresh Token 전달")
    @ApiResponse(
        responseCode = "200",
        description = "콜백 처리 성공",
        content = [Content(schema = Schema(implementation = TokenReissueResponseDto::class))]
    )
    fun oauthCallback(
        @Parameter(description = "Provider (google / kakao)") provider: String,
        @RequestParam("accessToken") accessToken: String,
        @RequestParam("refreshToken") refreshToken: String
    ): ResponseEntity<TokenReissueResponseDto>

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 DB에서 삭제")
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    fun withdraw(): ResponseEntity<Void>
}