package com.ruthlessprogramming.interviewapp.features.quiz.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.hannesdorfmann.mosby.mvp.MvpFragment
import com.ruthlessprogramming.interviewapp.R
import com.ruthlessprogramming.interviewapp.common.dagger.Injector
import com.ruthlessprogramming.interviewapp.extensions.disable
import com.ruthlessprogramming.interviewapp.extensions.enable
import com.ruthlessprogramming.interviewapp.features.quiz.presenter.QuizPresenter
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizState
import com.ruthlessprogramming.interviewapp.features.quiz.state.States
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.screen_quiz.iv1
import kotlinx.android.synthetic.main.screen_quiz.iv2
import kotlinx.android.synthetic.main.screen_quiz.iv3
import kotlinx.android.synthetic.main.screen_quiz.iv4
import kotlinx.android.synthetic.main.screen_quiz.quizToolbar
import kotlinx.android.synthetic.main.screen_quiz.tvAction
import kotlinx.android.synthetic.main.screen_quiz.tvCorrect
import kotlinx.android.synthetic.main.screen_quiz.tvIncorrect
import kotlinx.android.synthetic.main.screen_quiz.tvProgress
import kotlinx.android.synthetic.main.screen_quiz.tvQuestionTitle
import timber.log.Timber

class QuizViewImpl : MvpFragment<QuizView, QuizPresenter>(), QuizView {
    override fun showCompletion(state: QuizState) {
        Snackbar.make(
            quizToolbar,
            "You went ${state.correct} for ${state.currentQuizQuestions.size}! feel free to try again!",
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showQuizExpiration() {
        Toast.makeText(
            context,
            "Sorry, time has expired. Feel free to reset and try again",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun displayMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    private var previousIndex: Int? = null
    override fun render(quizState: QuizState) {
        when (quizState.state) {

             States.Initial -> {
                tvAction.text = getString(R.string.start)
                tvQuestionTitle.text = getString(R.string.prompt_quiz)
                quizToolbar.title = ""
                tvProgress.text = ""
                tvCorrect.text = ""
                tvIncorrect.text = ""
                tileList.forEach {
                    it.enable()
                    (it as ImageView).setImageDrawable(context?.getDrawable(R.drawable.empty_tile))
                }
            }

              States.Expired -> {
                tileList.forEach { it.disable() }
                quizToolbar.title = ""
                tvAction.text = getString(R.string.reset)
                tvQuestionTitle.disable()
                tvProgress.disable()
            }

             States.InProgress -> {
                quizToolbar.title = quizState.currentQuestionTimeRemaining.toString()
                tvQuestionTitle.apply {
                    tvQuestionTitle.text =
                            getString(R.string.question_text, quizState.getQuestionString())
                    enable()
                }

                tvProgress.apply {
                    text = quizState.getProgressString()
                    enable()
                }

                tvCorrect.apply {
                    text = getString(R.string.correct, quizState.correct)
                    enable()
                }

                tvIncorrect.apply {
                    text = getString(R.string.incorrect, quizState.incorrect)
                    enable()
                }

                tvAction.text = getString(R.string.reset)
                if (previousIndex != quizState.currentQuestionIndex) {
                    previousIndex = quizState.currentQuestionIndex
                    buildTiles(quizState)
                }
            }
        }
    }

    private val tileClickListener = View.OnClickListener { view ->
        tileList.forEach { it.disable() }
        presenter.onSelectionMade(view.tag as String)
    }

    private fun buildTiles(state: QuizState) {

        val shuffledUrls = state.getAnswerArray().shuffled()
        val url1 = shuffledUrls[0]
        val url2 = shuffledUrls[1]
        val url3 = shuffledUrls[2]
        val url4 = shuffledUrls[3]

        iv1.apply {
            enable()
            tag = url1
            setOnClickListener(tileClickListener)
            loadImageIntoTile(this, url1)
        }

        iv2.apply {
            enable()
            tag = url2
            setOnClickListener(tileClickListener)
            loadImageIntoTile(this, url2)
        }

        iv3.apply {
            enable()
            tag = url3
            setOnClickListener(tileClickListener)
            loadImageIntoTile(this, url3)
        }

        iv4.apply {
            enable()
            tag = url4
            setOnClickListener(tileClickListener)
            loadImageIntoTile(this, url4)
        }
    }

    private fun loadImageIntoTile(imageView: ImageView, url: String) {
        Picasso.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.empty_tile)
            .into(imageView)
    }

    private val quizPresenter = Injector.get().quizPresenter
    override fun createPresenter(): QuizPresenter = quizPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.screen_quiz, container, false)

    private lateinit var tileList: List<View>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(quizToolbar)
        tvAction.setOnClickListener {
            previousIndex = -1
            presenter.actionTapped()
        }
        tileList = listOf(iv1, iv2, iv3, iv4)
    }

    override fun onResume() {
        super.onResume()
        presenter.initializeQuizState()
    }
}
