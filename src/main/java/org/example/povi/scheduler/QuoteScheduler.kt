package org.example.povi.scheduler

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class QuoteScheduler (
    private val quoteService: QuoteService
) {

    companion object {
        private val log = LoggerFactory.getLogger(QuoteScheduler::class.java)
    }

    // 매일 자정(00:00:00)에 실행되는 스케줄러
    @Scheduled(cron = "0 47 14 * * *")
    fun fetchAndSaveDailyQuote() {
        log.info("매일 명언 스케줄러 실행: 외부 API를 통해 새로운 명언을 가져옵니다.")
        try {
            // 외부 API를 호출하고 DB에 저장하는 서비스 메서드를 실행
            quoteService.getQuote()
            log.info("오늘의 명언을 성공적으로 가져와 저장했습니다.")
        } catch (e: Exception) {  // @Scheduled 작업에서 허용 (스레드 중단 방지)
            log.error("매일 명언 스케줄러 실행 중 오류가 발생했습니다.", e)
        }
    }
}