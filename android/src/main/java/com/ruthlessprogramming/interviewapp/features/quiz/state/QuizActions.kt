package com.ruthlessprogramming.interviewapp.features.quiz.state

import zendesk.suas.Action

const val onStateAvailable = "on_state_available"
const val decrement = "decrement"
const val startQuiz = "start_quiz"
const val resetQuiz = "reset_quiz"
const val quizExpired = "quiz_expired"
const val quizFinished = "quiz_finished"
const val forward = "proceed_forward"
const val setRemainingTime = "set_remaining_time"
const val correctAnswer = "correct_answer"
const val incorrectAnswer = "incorrect_answer"

class QuizActions {
    class Init(val state: QuizState) :
        Action<QuizState>(
            onStateAvailable, state
        )

    class SetRemainingTime(val remainingTime: Long) : Action<Long>(setRemainingTime, remainingTime)

    object Decrement : Action<Unit>(decrement)
    object StartQuiz : Action<Unit>(startQuiz)
    object Forward : Action<Unit>(forward)
    object QuizExpired : Action<Unit>(quizExpired)
    object Reset : Action<Unit>(resetQuiz)
    object QuizFinished : Action<Unit>(quizFinished)
    object Correct : Action<Unit>(correctAnswer)
    object Incorrect : Action<Unit>(incorrectAnswer)
}