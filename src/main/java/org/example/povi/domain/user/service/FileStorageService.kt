package org.example.povi.domain.user.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileStorageService(
    @Value("\${file.upload.profile.dir}") uploadDir: String) {
    private val fileStorageLocation: Path = Paths.get(uploadDir).toAbsolutePath().normalize()

    init {
        try {
            Files.createDirectories(this.fileStorageLocation)
        } catch (ex: Exception) {
            throw RuntimeException("디렉토리를 생성할 수 없습니다.", ex)
        }
    }

    fun storeFile(file: MultipartFile): String {
        // 파일의 고유한 이름 생성
        val fileName = "${UUID.randomUUID()}_${file.originalFilename}"

        try {
            val targetLocation = fileStorageLocation.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
        } catch (ex: IOException) {
            throw RuntimeException("파일을 저장할 수 없습니다.", ex)
        }

        return "http://localhost:8080/images/profile/$fileName"
    }
}