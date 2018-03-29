package com.ruthlessprogramming.interviewapp.features.quiz.state

import org.threeten.bp.Instant
import zendesk.suas.Action
import zendesk.suas.Reducer
import javax.inject.Inject

class QuizReducer @Inject constructor() : Reducer<QuizState>() {
    private val secondsPerQuestion = 30L
    override fun getInitialState(): QuizState = QuizState()

    override fun reduce(state: QuizState, action: Action<*>): QuizState? {
        return when (action) {
            is QuizActions.Init -> action.state

            is QuizActions.SetRemainingTime -> state.copy(currentQuestionTimeRemaining = action.remainingTime)

            is QuizActions.StartQuiz -> {
                val shuffledQuestions = state.questionMap.keys.shuffled()
                state.copy(
                    state = States.InProgress,
                    currentQuizQuestions = shuffledQuestions,
                    currentQuestionIndex = 0,
                    currentQuestionTimeRemaining = secondsPerQuestion,
                    timeToExpire = Instant.now().plusSeconds(secondsPerQuestion).toEpochMilli(),
                    correct = 0,
                    incorrect = 0
                )
            }

            is QuizActions.Decrement -> state.copy(
                currentQuestionTimeRemaining = state.currentQuestionTimeRemaining.minus(
                    1
                )
            )

            is QuizActions.Forward -> state.copy(
                currentQuestionIndex = state.currentQuestionIndex.plus(
                    1
                ),
                currentQuestionTimeRemaining = secondsPerQuestion,
                timeToExpire = Instant.now().plusSeconds(secondsPerQuestion).toEpochMilli()
            )

            is QuizActions.Incorrect -> state.copy(incorrect = state.incorrect.plus(1))
            is QuizActions.Correct -> state.copy(correct = state.correct.plus(1))

            is QuizActions.QuizExpired -> state.copy(
                state = States.Expired,
                timeToExpire = Long.MAX_VALUE,
                currentQuestionTimeRemaining = secondsPerQuestion
            )
            is QuizActions.QuizFinished -> state.copy(
                state = States.Initial,
                timeToExpire = Long.MAX_VALUE,
                currentQuestionTimeRemaining = secondsPerQuestion,
                currentQuestionIndex = 0

            )

            is QuizActions.Reset -> {
                val shuffledQuestions = state.questionMap.keys.shuffled()
                state.copy(
                    state = States.Initial,
                    currentQuizQuestions = shuffledQuestions,
                    currentQuestionIndex = 0,
                    currentQuestionTimeRemaining = secondsPerQuestion,
                    timeToExpire = Instant.now().plusSeconds(secondsPerQuestion).toEpochMilli(),
                    correct = 0,
                    incorrect = 0
                )
            }

            else -> state
        }
    }
}