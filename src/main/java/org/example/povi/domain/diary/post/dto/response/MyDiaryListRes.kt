package org.example.povi.domain.diary.post.dto.response


data class MyDiaryListRes(
        val totalCount: Long,
        val thisWeekCount: Long,
        val moodSummary: MoodSummaryRes,

        val myDiaries: List<MyDiaryCardRes?>
) {

}