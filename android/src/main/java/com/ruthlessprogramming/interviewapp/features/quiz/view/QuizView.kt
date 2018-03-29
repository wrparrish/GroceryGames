package com.ruthlessprogramming.interviewapp.features.quiz.view

import com.hannesdorfmann.mosby.mvp.MvpView
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizState

interface QuizView : MvpView {
    fun render(state: QuizState)
    fun showQuizExpiration()
    fun displayMessage(s: String)
    fun showCompletion(state: QuizState)
}