package org.example.povi.domain.diary.post.mapper

import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq
import org.example.povi.domain.diary.post.entity.DiaryPost
import org.example.povi.domain.user.entity.User

object DiaryRequestMapper {
    /**
     * DiaryCreateReq → DiaryEntry 엔티티 변환
     */
    @JvmStatic
    fun fromCreateRequest(req: DiaryPostCreateReq, author: User?): DiaryPost {
        return DiaryPost.create(author!!, req.title, req.content, req.moodEmoji, req.visibility, req.imageUrls)
    }
}