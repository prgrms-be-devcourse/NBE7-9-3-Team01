package org.example.povi.global.mapper

import org.example.povi.domain.user.dto.ProfileRes
import org.example.povi.domain.user.dto.ProfileUpdateReq
import org.example.povi.domain.user.entity.User
import org.mapstruct.*

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
abstract class UserMapper {

    /**
     * 프로필 수정 요청 → User 업데이트
     * null 값은 기존 값 유지
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun updateUserFromDto(
        @MappingTarget user: User,
        reqDto: ProfileUpdateReq
    )

    /**
     * 커스텀 업데이트 (User 엔티티의 updateNickname(), updateBio() 메서드 호출)
     */
    @AfterMapping
    open fun applyCustomUpdates(
        @MappingTarget user: User,
        reqDto: ProfileUpdateReq
    ) {
        reqDto.nickname?.let { user.updateNickname(it) }
        reqDto.bio?.let { user.updateBio(it) }
    }

    /**
     * User → ProfileRes 변환
     */
    abstract fun toProfileRes(user: User): ProfileRes
}