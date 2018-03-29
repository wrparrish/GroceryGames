package com.ruthlessprogramming.interviewapp.features.quiz.model

import com.ruthlessprogramming.interviewapp.common.state.SuasStoreModel
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizState
import zendesk.suas.Store
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizModel @Inject constructor(private val s: Store) : SuasStoreModel<QuizState>() {
    override fun setStore(): Store = s

    override fun getModelState(): QuizState {
        val state = store.state.getState(QuizState::class.java)
        return state ?: QuizState()
    }
}