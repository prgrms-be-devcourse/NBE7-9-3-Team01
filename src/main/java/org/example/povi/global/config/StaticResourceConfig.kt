package org.example.povi.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 정적 리소스 매핑 설정
 * 업로드된 이미지 파일 경로를 URL로 매핑하여 외부 접근을 가능하게 함.
 */
@Configuration
class StaticResourceConfig : WebMvcConfigurer {

    @Value("\${file.upload.diary.dir}")
    lateinit var diaryDir: String

    @Value("\${file.upload.community.dir}")
    lateinit var communityDir: String

    @Value("\${file.upload.profile.dir}")
    lateinit var profileDir: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {

        registry.addResourceHandler("/images/diary/**")
            .addResourceLocations("file:$diaryDir/")

        registry.addResourceHandler("/images/community/**")
            .addResourceLocations("file:$communityDir/")

        registry.addResourceHandler("/images/profile/**")
            .addResourceLocations("file:$profileDir/")
    }
}