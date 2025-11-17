package org.example.povi.domain.diary.image.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class DiaryImageUploadService {
    @Value("\${file.upload.diary.dir}")
    private val uploadDir: String? = null // 예: /Users/you/povi-uploads

    private val log: Logger = LoggerFactory.getLogger(DiaryImageUploadService::class.java)
    /**
     * 다이어리용 이미지 업로드
     * - 로컬 폴더에 저장하고 /images/{uuid.ext} URL 목록 반환
     */
    fun upload(files: List<MultipartFile>?): List<String> {
        val imageUrls: MutableList<String> = ArrayList()
        if (files == null || files.isEmpty()) return imageUrls

        val directory = File(uploadDir)
        check(!(!directory.exists() && !directory.mkdirs())) { "업로드 경로 생성 실패: $uploadDir" }

        for (file in files) {
            if (file.isEmpty) continue

            val contentType = file.contentType
            require(!(contentType == null || !contentType.startsWith("image/"))) { "이미지 파일만 업로드할 수 있습니다." }

            val extension = getExt(Objects.requireNonNull(file.originalFilename, "파일명 없음"))
            val stored = UUID.randomUUID().toString() + "." + extension

            try {
                file.transferTo(File(fullPath(stored)))
                imageUrls.add("/images/diary/$stored")
            } catch (e: IOException) {
                log.error("Diary image delete failed: {}", e.message)
                throw RuntimeException("다이어리 이미지 업로드 실패", e)
            }
        }
        return imageUrls
    }

    /**
     * 다이어리 이미지 삭제 (URL → 실제 파일 경로 변환 후 삭제)
     */
    fun deleteByUrl(imageUrl: String) {
        val filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1)
        val path = Paths.get(fullPath(filename))
        try {
            Files.deleteIfExists(path)
        } catch (e: IOException) {
            log.error("Diary image delete failed: {}", e.message)
            throw RuntimeException("다이어리 이미지 삭제 실패", e)
        }
    }

    private fun fullPath(fileName: String): String {
        return uploadDir + File.separator + fileName
    }

    private fun getExt(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        require(dotIndex >= 0) { "확장자를 찾을 수 없습니다: $fileName" }
        return fileName.substring(dotIndex + 1)
    }
}
