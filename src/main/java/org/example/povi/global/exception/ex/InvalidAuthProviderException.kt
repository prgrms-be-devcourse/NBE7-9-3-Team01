package org.example.povi.global.exception.ex

import org.example.povi.global.exception.error.ErrorCode

/**
 * OAuth2 인증 제공자 정보가 유효하지 않을 경우 발생하는 예외
 * - 지원하지 않는 provider(KAKAO, GOOGLE 외)일 때 발생
 */
class InvalidAuthProviderException :
    CustomException(ErrorCode.INVALID_AUTH_PROVIDER)