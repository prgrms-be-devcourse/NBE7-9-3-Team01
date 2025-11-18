package org.example.povi.domain.diary.enums

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

enum class MoodEmoji(val label: String, val valence: Int) {
    HAPPY("😊 행복해요", 10),
    JOYFUL("😂 즐거워요", 8),
    CALM("😌 평온해요", 4),
    NEUTRAL("😐 그저 그래요", 0),
    DEPRESSED("😔 우울해요", -4),
    SAD("😢 슬퍼요", -6),
    TIRED("😭 힘들어요", -8),
    ANGRY("😤 화나요", -10);

    fun label(): String {
        return label
    }

    fun valence(): Int {
        return valence
    }

    companion object {
        //평균 점수와 가장 가까운 감정 반환
        @JvmStatic
        fun fromValence(averageScore: Double): MoodEmoji {
            val clampedScore = max(-10.0, min(10.0, averageScore))
            var mostSimilarEmotion = MoodEmoji.NEUTRAL
            var smallestDifference = Double.MAX_VALUE
            for (m in MoodEmoji.entries) {
                val d = abs(m.valence - clampedScore)
                if (d < smallestDifference) {
                    smallestDifference = d
                    mostSimilarEmotion = m
                }
            }
            return mostSimilarEmotion
        }
    }
}