package org.example.povi.domain.user.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.domain.user.controller.docs.UserControllerDocs
import org.example.povi.domain.user.dto.MyPageRes
import org.example.povi.domain.user.dto.ProfileRes
import org.example.povi.domain.user.dto.ProfileUpdateReq
import org.example.povi.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/me")
class UserController (
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) : UserControllerDocs{

    private fun resolveToken(bearerToken: String?): String {
        return bearerToken!!.replace("Bearer ", "")
    }


    @GetMapping("/myPage")
    override fun getMyPage(
        @RequestHeader("Authorization") bearerToken: String
    ): ResponseEntity<MyPageRes> {
        val token = resolveToken(bearerToken)
        val userId = jwtTokenProvider.getUserId(token)
        val responseDto = userService.getMyPage(userId)
        return ResponseEntity.ok(responseDto)
    }

    @PatchMapping("/updateProfile")
    override fun updateProfile(
        @RequestHeader("Authorization") bearerToken: String,
        @RequestPart("dto") reqDto: @Valid ProfileUpdateReq,
        @RequestPart(value = "image", required = false) imageFile: MultipartFile?
    ): ResponseEntity<ProfileRes> {
        val token = resolveToken(bearerToken)
        val userId = jwtTokenProvider.getUserId(token)
        val responseDto = userService.updateProfile(userId, reqDto, imageFile)
        return ResponseEntity.ok(responseDto)
    }
}
