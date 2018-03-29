package com.ruthlessprogramming.interviewapp.features.quiz.presenter

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.ruthlessprogramming.interviewapp.features.quiz.model.QuizModel
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizActions
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizState
import com.ruthlessprogramming.interviewapp.features.quiz.state.States
import com.ruthlessprogramming.interviewapp.features.quiz.view.QuizView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import zendesk.suas.Listener
import zendesk.suas.State
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizPresenter @Inject constructor(private val model: QuizModel) :
    MvpBasePresenter<QuizView>() {

    private var timerDisposable: CompositeDisposable = CompositeDisposable()
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val questionDelayMS = 1500L

    fun onSelectionMade(selection: String) {
        if (model.getModelState().state == States.Expired) {
            return
        }

        stopInterval()

        val correctAnswer = model.getModelState().getAnswerArray().first()
        if (correctAnswer == selection) {
            view?.displayMessage("Correct!")
            model.dispatch(QuizActions.Correct)
        } else {
            view?.displayMessage("Incorrect!  lets try another.")
            model.dispatch(QuizActions.Incorrect)
        }

        executor.schedule({
            setupNextQuestion(model.getModelState())
        }, questionDelayMS, TimeUnit.MILLISECONDS)
    }

    private fun setupNextQuestion(modelState: QuizState) = with(modelState) {
        if (currentQuestionIndex == currentQuizQuestions.size - 1) {
            stopInterval()
            model.dispatch(QuizActions.QuizFinished)
            view?.showCompletion(model.getModelState())
        } else {
            model.dispatch(QuizActions.Forward)
            startInterval()
        }
    }

    private val stateListener: Listener<State> = Listener { appState ->
        val state = appState.getState(QuizState::class.java)
        if (isViewAttached && state != null) {
            view?.render(state)
        }
    }

    override fun attachView(view: QuizView?) {
        super.attachView(view)
        model.addListener(stateListener)
    }

    fun initializeQuizState() {
        model.getModelState().apply {
            if (state == States.InProgress) {
                checkAndResume(this)
            } else {
                model.dispatch(QuizActions.Reset)
            }
        }
    }

     fun checkAndResume(quizState: QuizState) {
        val expirationInstant = Instant.ofEpochMilli(quizState.timeToExpire)
        val diffInSeconds = Duration.between(Instant.now(), expirationInstant).seconds

        if (diffInSeconds > 0L) {
            model.dispatch(QuizActions.SetRemainingTime(diffInSeconds))
            startInterval()
        } else {
            expire()
        }
    }

    private fun expire() {
        stopInterval()
        model.dispatch(QuizActions.QuizExpired)
        view?.showQuizExpiration()
    }

    private fun timerObservable() =
        Observable.interval(1, TimeUnit.SECONDS)
            .filter { model.getModelState().state != States.Expired }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                // ugly side effect code, but i'm running out of time.
                model.dispatch(QuizActions.Decrement)
                if (model.getModelState().currentQuestionTimeRemaining <= 0) {
                    view?.showQuizExpiration()
                    model.dispatch(QuizActions.QuizExpired)
                }
            }

    private fun startInitialQuiz() {
        model.dispatch(QuizActions.StartQuiz)
        startInterval()
    }

    override fun detachView(retainInstance: Boolean) {
        super.detachView(retainInstance)
        model.removeListener(stateListener)
        stopInterval()
    }

    private fun resetQuiz() {
        model.dispatch(QuizActions.Reset)
    }

    private fun startInterval() {
        timerDisposable.add(timerObservable().subscribe())
    }

    fun actionTapped() = with(model.getModelState()) {
        stopInterval()
        when (state) {
             States.Expired -> resetQuiz()
             States.InProgress -> resetQuiz()
             States.Initial -> startInitialQuiz()
        }
    }

    private fun stopInterval() {
        timerDisposable.clear()
    }
}
