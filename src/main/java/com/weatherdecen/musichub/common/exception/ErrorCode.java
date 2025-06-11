package com.weatherdecen.musichub.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    JSON_PASING_ERROR("WEATHER-01", "레디스 파싱 중 오류가 있습니다.", "데이터를 불러오는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    YOUTUBE_NOT_FOUND("YOUTUBE-01", "존재하지 않는 유튜브 아이디 값입니다.", "유튜브 정보를 불러오는 데 실패했습니다.", HttpStatus.NO_CONTENT),
    YOUTUBE_SEARCH_ERROR("YOUTUBE-02", "유튜브 검색 결과가 존재하지 않습니다.", "유튜브 검색 결과를 불러오는 데 실패했습니다.", HttpStatus.NO_CONTENT),
    VALIDATION_ERROR("COMMON-01", "유효성 검증에 실패했습니다.", "정보를 불러오는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("COMMON-02", "필수 파라미터가 누락되었습니다.", "정보를 불러오는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("COMMON-03", "내부 예외가 발생했습니다.", "정보를 불러오는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String internalErrorMessage;
    private final String externalErrorMessage;
    private final HttpStatus httpStatus;
}
