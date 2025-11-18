package org.example.povi.domain.community.repository

import org.example.povi.domain.community.entity.CommunityImage
import org.example.povi.domain.community.entity.CommunityPost
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityImageRepository : JpaRepository<CommunityImage, Long> {
    fun deleteAllByCommunityPost(communityPost: CommunityPost?)
}
