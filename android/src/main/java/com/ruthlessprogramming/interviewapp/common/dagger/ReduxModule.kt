package com.ruthlessprogramming.interviewapp.common.dagger

import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizReducer
import dagger.Module
import dagger.Provides
import zendesk.suas.Store
import zendesk.suas.Suas
import javax.inject.Singleton

@Module
class ReduxModule {

    @Provides
    @Singleton
    fun provideStore(
        quizReducer: QuizReducer
    ): Store {
        return Suas.createStore(
            quizReducer
        ).build()
    }
}
