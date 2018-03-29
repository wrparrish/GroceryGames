package com.ruthlessprogramming.interviewapp.features.quiz.state


data class QuizState(

    val state: States = States.Initial,
    val questionMap: Map<String, List<String>> = mapOf(),
    val currentQuizQuestions: List<String> = listOf(),
    val currentQuestionIndex: Int = -1,
    val currentQuestionTimeRemaining: Long = 30,
    val timeToExpire: Long = Long.MAX_VALUE,
    val correct: Int = 0,
    val incorrect: Int = 0
) {

    fun getQuestionString() = currentQuizQuestions[currentQuestionIndex]

    fun getAnswerArray() = questionMap.getOrDefault(getQuestionString(), listOf())

    fun getProgressString() = "${currentQuestionIndex + 1} / ${currentQuizQuestions.size}"


}


enum class States {
    Initial,
    InProgress,
    Expired;
}