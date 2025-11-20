package org.example.povi.domain.transcription.service
import org.example.povi.domain.quote.repository.QuoteRepository
import org.example.povi.domain.transcription.dto.TranscriptionPageRes
import org.example.povi.domain.transcription.dto.TranscriptionReq
import org.example.povi.domain.transcription.dto.TranscriptionRes
import org.example.povi.domain.transcription.entity.Transcription
import org.example.povi.domain.transcription.repository.TranscriptionRepository
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.exception.ex.AuthorizationException
import org.example.povi.global.exception.ex.DuplicateTranscriptionException
import org.example.povi.global.exception.ex.ResourceNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TranscriptionService (
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val transcriptionRepository: TranscriptionRepository
) {
    // 필사 저장
    @Transactional
    fun createTranscription(userId: Long, quoteId: Long, transcriptionReq: TranscriptionReq): TranscriptionRes {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다: ID $userId") }
        val quote = quoteRepository.findById(quoteId)
            .orElseThrow { ResourceNotFoundException("명언을 찾을 수 없습니다: ID $quoteId") }

        val transcription = Transcription(transcriptionReq.content, quote, user)

        try {
            val savedTranscription = transcriptionRepository.save(transcription)
            return TranscriptionRes(savedTranscription)
        } catch (e: DataIntegrityViolationException) {
            // DB의 유니크 제약 조건 위반 시 이 예외가 발생
            throw DuplicateTranscriptionException("이미 필사한 명언입니다.")
        }
    }

    // 필사 삭제
    @Transactional
    fun deleteTranscription(userId: Long, transcriptionId: Long) {
        val transcription = transcriptionRepository.findById(transcriptionId)
            .orElseThrow { ResourceNotFoundException("해당 필사 기록을 찾을 수 없습니다.") }

        if (transcription.user.id != userId) {
            throw AuthorizationException("삭제 권한이 없습니다.")
        }

        transcriptionRepository.delete(transcription)
    }

    // 필사기록 조회
    @Transactional(readOnly = true)
    fun getMyTranscriptions(userId: Long, pageable: Pageable): TranscriptionPageRes {
        val transcriptionsPage: Page<Transcription> =
            transcriptionRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)

        return TranscriptionPageRes(transcriptionsPage)
    }
}
