package org.example.povi.domain.user.service

import org.example.povi.domain.diary.post.service.DiaryPostService
import org.example.povi.domain.transcription.service.TranscriptionService
import org.example.povi.domain.user.dto.MyPageRes
import org.example.povi.domain.user.dto.ProfileRes
import org.example.povi.domain.user.dto.ProfileUpdateReq
import org.example.povi.domain.user.repository.UserRepository
import org.example.povi.global.exception.ex.ResourceNotFoundException
import org.example.povi.global.mapper.UserMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserService (
    private val userRepository: UserRepository,
    private val diaryPostService: DiaryPostService,
    private val transcriptionService: TranscriptionService,
    private val fileStorageService: FileStorageService,
    private val userMapper: UserMapper
    ){
    @Transactional(readOnly = true) // 마이페이지 조회
    fun getMyPage(userId: Long): MyPageRes {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다.") }
        val profileRes = userMapper.toProfileRes(user)

        val diaryCount = diaryPostService.getDiaryPostCountForUser(userId)
        val previewPageable: Pageable = PageRequest.of(0, 4)
        val transcriptionList = transcriptionService.getMyTranscriptions(userId, previewPageable)

        return MyPageRes(profileRes, diaryCount, transcriptionList)
    }

    @Transactional // 프로필 수정
    fun updateProfile(userId: Long, reqDto: ProfileUpdateReq, image: MultipartFile?): ProfileRes {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("사용자를 찾을 수 없습니다.") }

        userMapper.updateUserFromDto(user, reqDto)

        if (image != null && !image.isEmpty) {
            val imageUrl = fileStorageService.storeFile(image)
            user.updateProfileImgUrl(imageUrl)
        }

        return userMapper.toProfileRes(user)
    }
}
