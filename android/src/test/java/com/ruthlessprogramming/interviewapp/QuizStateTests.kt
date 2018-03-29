package com.ruthlessprogramming.interviewapp

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.ruthlessprogramming.interviewapp.features.quiz.model.QuizModel
import com.ruthlessprogramming.interviewapp.features.quiz.presenter.QuizPresenter
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizActions
import com.ruthlessprogramming.interviewapp.features.quiz.view.QuizView
import org.junit.Before
import org.junit.Test
import java.time.Instant


class QuizStateTests : JvmStateTest() {


    private val model = QuizModel(store)
    private val givenState = model.getModelState()
    private val presenter = QuizPresenter(model)
    private val mockedView = mock<QuizView> {}
    private var givenMap = getQuestionMap()


    @Before
    fun setup() {
        presenter.attachView(mockedView)
        givenMap = getQuestionMap()
        model.dispatch(QuizActions.Init(givenState.copy(questionMap = givenMap)))
    }


    @Test
    fun `a quiz should have shuffled order of questions`() {
        val givenOrder = givenMap.keys.toList()

        model.dispatch(QuizActions.StartQuiz)
        val resolvedQuestions = model.getModelState().currentQuizQuestions

        assert(givenOrder != resolvedQuestions)
    }

    @Test
    fun `forward should increment index when not at end of questions`() {
        val givenIndex = model.getModelState().currentQuestionIndex
        model.dispatch(QuizActions.Forward)
        val resolvedIndex = model.getModelState().currentQuestionIndex
        assert(resolvedIndex > givenIndex)
    }

    @Test
    fun `the index should  reset when the quiz battery is complete`() {
        model.dispatch(QuizActions.StartQuiz)
        val givenIndex = model.getModelState().currentQuizQuestions.size - 1
        model.dispatch(QuizActions.Init(model.getModelState().copy(currentQuestionIndex = givenIndex)))
        model.dispatch(QuizActions.QuizFinished)
        val resolvedIndex = model.getModelState().currentQuestionIndex
        assert(resolvedIndex < givenIndex)
    }

    @Test
    fun `selection should not affect state when the state is expired`() {
        model.dispatch(QuizActions.QuizExpired)
        val givenState = model.getModelState()
        presenter.onSelectionMade("givenString")
        val resolvedState = model.getModelState()
        assert(givenState == resolvedState)
    }

    @Test
    fun `it should respect expiration`() {
        val givenExpiration = Instant.now().minusSeconds(10).toEpochMilli()
        model.dispatch(QuizActions.Init(model.getModelState().copy(timeToExpire = givenExpiration)))
        val resolvedState = model.getModelState()
        presenter.checkAndResume(resolvedState)
        verify(mockedView, times(1)).showQuizExpiration()
    }

    @Test
    fun `it should notify user of success`() {
        model.dispatch(QuizActions.StartQuiz)
        val givenAnswer = model.getModelState().getAnswerArray().first()
        presenter.onSelectionMade(givenAnswer)
        verify(mockedView, times(1)).displayMessage("Correct!")
    }

    @Test
    fun `it should notify user of failure`() {
        model.dispatch(QuizActions.StartQuiz)
        val givenAnswer = model.getModelState().getAnswerArray().last()
        presenter.onSelectionMade(givenAnswer)
        verify(mockedView, times(1)).displayMessage("Incorrect!  lets try another.")
    }

    @Test
    fun `decrement should reduce current question time`() {
        model.dispatch(QuizActions.StartQuiz)
        val givenTime = model.getModelState().currentQuestionTimeRemaining
        model.dispatch(QuizActions.Decrement)
        val resolvedTime = model.getModelState().currentQuestionTimeRemaining
        assert(resolvedTime < givenTime)
    }

    @Test
    fun `a participate should have 30 seconds to respond`() {
        model.dispatch(QuizActions.StartQuiz)
        val resolvedTimeRemaining = model.getModelState().currentQuestionTimeRemaining
        assert(resolvedTimeRemaining == 30L)
    }

}