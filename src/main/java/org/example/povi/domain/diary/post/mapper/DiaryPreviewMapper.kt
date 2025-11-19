package org.example.povi.domain.diary.post.mapper

import org.example.povi.domain.diary.post.entity.DiaryPost

object DiaryPreviewMapper {
    // 첫 번째 이미지 URL (없으면 null)
    @JvmStatic
    fun firstImageUrl(diary: DiaryPost): String? {
        return if (diary.images.isEmpty()) null else diary.images[0].imageUrl
    }

    // 미리보기 텍스트 (공백 정리 + 길이 제한)
    @JvmStatic
    fun buildPreviewText(content: String?, maxLength: Int): String {
        if (content == null || content.isBlank()) return ""
        val compact = content.replace("\\s+".toRegex(), " ").trim { it <= ' ' }
        return if (compact.length <= maxLength) compact else compact.substring(0, maxLength) + "..."
    }
}
