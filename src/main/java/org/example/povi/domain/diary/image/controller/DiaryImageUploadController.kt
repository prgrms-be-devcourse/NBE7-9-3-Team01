package org.example.povi.domain.diary.image.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.example.povi.domain.diary.image.service.DiaryImageUploadService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/diary-images")
class DiaryImageUploadController(

        private val diaryImageUploadService: DiaryImageUploadService
) {

    /**
     * 다이어리 이미지 업로드
     */
    @Operation(summary = "일기 이미지 업로드", description = "여러 개의 일기 이미지를 업로드하고 URL 목록을 반환합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "업로드 성공", content = [Content(array = ArraySchema(schema = Schema(implementation = String::class)))]), ApiResponse(responseCode = "400", description = "잘못된 요청 (e.g., 이미지 파일 없음)")])
    @PostMapping
    fun uploadDiaryImages(
            @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<List<String>> {
        val uploadedUrls = diaryImageUploadService.upload(images)
        return ResponseEntity.ok(uploadedUrls)
    }

    /**
     * 다이어리 이미지 삭제
     */
    @Operation(summary = "일기 이미지 삭제", description = "URL을 이용해 업로드된 일기 이미지를 삭제합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "204", description = "삭제 성공"), ApiResponse(responseCode = "400", description = "잘못된 URL"), ApiResponse(responseCode = "404", description = "삭제할 이미지를 찾을 수 없음")])
    @DeleteMapping
    fun deleteDiaryImage(
            @RequestParam("imageUrl") imageUrl: String
    ): ResponseEntity<Void> {
        diaryImageUploadService.deleteByUrl(imageUrl)
        return ResponseEntity.noContent().build()
    }
}