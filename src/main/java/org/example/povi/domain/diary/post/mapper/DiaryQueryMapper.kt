package org.example.povi.domain.diary.post.mapper


/**
 * JPQL의 집계 결과(Object[])를
 * (postId → count) 형태의 Map으로 변환하는 유틸리티 클래스.
 */
object DiaryQueryMapper {
    fun toCountMap(rows: List<Array<Any>>?): Map<Long, Long> {
        val m: MutableMap<Long, Long> = HashMap()
        if (rows == null) return m
        for (r in rows) {
            m[r[0] as Long] = r[1] as Long
        }
        return m
    }
}