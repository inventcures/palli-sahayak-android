package com.pallisahayak.core.evaluation

object SusQuestionnaire {

    data class SusItem(val number: Int, val text: String, val isPositive: Boolean)

    val items = listOf(
        SusItem(1, "I think that I would like to use this system frequently", true),
        SusItem(2, "I found the system unnecessarily complex", false),
        SusItem(3, "I thought the system was easy to use", true),
        SusItem(4, "I think that I would need the support of a technical person to use this system", false),
        SusItem(5, "I found the various functions in this system were well integrated", true),
        SusItem(6, "I thought there was too much inconsistency in this system", false),
        SusItem(7, "I would imagine that most people would learn to use this system very quickly", true),
        SusItem(8, "I found the system very cumbersome to use", false),
        SusItem(9, "I felt very confident using the system", true),
        SusItem(10, "I needed to learn a lot of things before I could get going with this system", false),
    )

    fun calculateScore(responses: List<Int>): Float {
        require(responses.size == 10) { "SUS requires exactly 10 responses" }
        require(responses.all { it in 1..5 }) { "Each response must be 1-5" }

        var sum = 0
        responses.forEachIndexed { index, score ->
            sum += if (items[index].isPositive) score - 1 else 5 - score
        }
        return sum * 2.5f
    }

    fun getInterpretation(score: Float): String = when {
        score >= 80.3 -> "Excellent (Grade A)"
        score >= 68.0 -> "Good (Grade B)"
        score >= 51.0 -> "OK (Grade C)"
        score >= 35.7 -> "Poor (Grade D)"
        else -> "Awful (Grade F)"
    }
}
