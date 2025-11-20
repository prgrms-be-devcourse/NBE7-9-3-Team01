package org.example.povi.domain.transcription.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.example.povi.auth.token.jwt.JwtTokenProvider
import org.example.povi.domain.transcription.controller.docs.TranscriptionControllerDocs
import org.example.povi.domain.transcription.dto.TranscriptionPageRes
import org.example.povi.domain.transcription.dto.TranscriptionReq
import org.example.povi.domain.transcription.dto.TranscriptionRes
import org.example.povi.domain.transcription.service.TranscriptionService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transcriptions")
class TranscriptionController (
    private val transcriptionService: TranscriptionService,
    private val jwtTokenProvider: JwtTokenProvider
) : TranscriptionControllerDocs {
    private fun String.resolveToken(): String = this.removePrefix("Bearer ")

    @PostMapping("/{quoteId}")
    override fun createTranscription(
        @PathVariable quoteId: Long,
        @Valid @RequestBody reqDto: TranscriptionReq,
        @RequestHeader("Authorization") bearerToken: String
    ): ResponseEntity<TranscriptionRes> {
        val token = bearerToken.resolveToken()
        val userId = jwtTokenProvider.getUserId(token)
        val responseDto = transcriptionService.createTranscription(userId, quoteId, reqDto!!)

        return ResponseEntity.ok(responseDto)
    }

    @DeleteMapping("/{transcriptionId}")
    override fun deleteTranscription(
        @PathVariable transcriptionId: Long,
        @RequestHeader("Authorization") bearerToken: String
    ): ResponseEntity<Void> {
        val token = bearerToken.resolveToken()
        val userId = jwtTokenProvider.getUserId(token)
        transcriptionService.deleteTranscription(userId, transcriptionId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/me") // 본인이 작성한 필사기록 조회
    override fun getMyTranscriptions(
        @RequestHeader("Authorization") bearerToken: String,
        @PageableDefault(size = 4, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<TranscriptionPageRes> {
        val token = bearerToken.resolveToken()
        val userId = jwtTokenProvider.getUserId(token)
        val responseDto = transcriptionService.getMyTranscriptions(userId, pageable)
        return ResponseEntity.ok(responseDto)
    }
}
