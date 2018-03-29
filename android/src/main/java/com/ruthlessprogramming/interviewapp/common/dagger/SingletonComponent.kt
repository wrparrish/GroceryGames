package com.ruthlessprogramming.interviewapp.common.dagger

import com.google.gson.Gson
import com.ruthlessprogramming.interviewapp.features.quiz.presenter.QuizPresenter
import dagger.Component
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import zendesk.suas.Store
import javax.inject.Singleton

@Singleton
@Component(modules = [(ContextModule::class), (NavigationModule::class), (ReduxModule::class)])
interface SingletonComponent {
    val cicerone: Cicerone<Router>
    val quizPresenter: QuizPresenter
    val store: Store
    val gson: Gson
}