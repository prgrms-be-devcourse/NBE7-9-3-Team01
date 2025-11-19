package org.example.povi.domain.community.service

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
class FileUploadService {
    @Value("\${file.upload.community.dir}")
    private val uploadDir: String? = null

    private val log: Logger = LoggerFactory.getLogger(FileUploadService::class.java)

    fun uploadFiles(multipartFiles: List<MultipartFile>): List<String> {
        val storedFilePaths: MutableList<String> = ArrayList()
        for (file in multipartFiles) {
            if (file.isEmpty) {
                continue
            }

            try {
                val originalFilename = file.originalFilename
                val storedFilename = createStoredFilename(originalFilename)

                val uploadPath = File(uploadDir)
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs()
                }
                file.transferTo(File(getFullPath(storedFilename)))

                storedFilePaths.add("/images/$storedFilename")
            } catch (e: IOException) {
                log.error("File upload failed: {}", e.message)
                throw RuntimeException("파일 업로드에 실패했습니다.", e)
            }
        }
        return storedFilePaths
    }

    fun deleteFile(fileUrl: String) {
        val filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1)
        val filePath = Paths.get(getFullPath(filename))
        try {
            Files.deleteIfExists(filePath)
        } catch (e: IOException) {
            log.error("File deletion failed: {}", e.message)
            throw RuntimeException("파일 삭제에 실패했습니다.", e)
        }
    }

    private fun getFullPath(filename: String): String {
        return uploadDir + File.separator + filename
    }

    private fun createStoredFilename(originalFilename: String): String {
        val ext = extractExt(originalFilename)
        val uuid = UUID.randomUUID().toString()
        return "$uuid.$ext"
    }

    private fun extractExt(originalFilename: String): String {
        val pos = originalFilename.lastIndexOf(".")
        return originalFilename.substring(pos + 1)
    }
}