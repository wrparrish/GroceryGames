package com.ruthlessprogramming.interviewapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ruthlessprogramming.interviewapp.common.dagger.Injector
import com.ruthlessprogramming.interviewapp.common.dagger.screen_main
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizActions
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizState
import com.ruthlessprogramming.interviewapp.features.quiz.view.QuizViewImpl
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cicerone.navigatorHolder.setNavigator(navigator)
        setContentView(R.layout.activity_main)

        readStateAsync(this) { existingState ->
            store.dispatch(QuizActions.Init(existingState))
            cicerone.router.newRootScreen(screen_main, existingState)
        }
    }

    override fun onStop() {
        queryAndSaveState()
        super.onStop()
    }
    
    private val store = Injector.get().store
    private fun queryAndSaveState() {
        val quizState = store.state.getState(QuizState::class.java)
        quizState?.let { state ->
            if (isFinishing) { // user  backed out / chose to leave
                saveState(QuizState())
            } else {
                saveState(state) // user has data we want to save
            }
        }
    }

    private val prefState = "state_key"
    private val gson: Gson by lazy { Injector.get().gson }
    private fun saveState(state: QuizState) {
        Timber.v("savingState  $state")
        PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
            putString(prefState, gson.toJson(state))
        }.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        cicerone.navigatorHolder.removeNavigator()
        Timber.v("onDestroy Called isFinishing = $isFinishing")
    }

    /****
     *  Navigation Abstraction Code
     */

    private val cicerone: Cicerone<Router> = Injector.get().cicerone
    private val navigator: Navigator = object : SupportAppNavigator(this, R.id.fragmentContainer) {
        override fun createActivityIntent(screenKey: String, data: Any?): Intent? {
            return null
        }

        override fun createFragment(screenKey: String, data: Any?): Fragment? {
            return when (screenKey) {
                screen_main -> {
                    QuizViewImpl()
                }

                else -> null
            }
        }
    }

    // asset parsing code

    private fun readStateAsync(
        context: Context,
        block: (QuizState) -> Unit
    ) =
        launch(UI) {

            val state = async {

                val savedState = async {
                    persistedState()
                }.await()

                if (savedState.questionMap.isNotEmpty()) {
                    savedState
                } else {

                    val jsonString = loadJSONFromAsset(context)
                    val type = object : TypeToken<Map<String, List<String>>>() {}.type
                    val questionsMap: Map<String, List<String>> = gson.fromJson(jsonString, type)
                    QuizState(questionMap = questionsMap)
                }
            }.await()

            block(state)
        }

    private fun loadJSONFromAsset(context: Context): String? {
        var json: String?
        try {
            val `is` = context.getAssets().open("questions.json")

            val size = `is`.available()

            val buffer = ByteArray(size)

            `is`.read(buffer)

            `is`.close()

            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    lateinit var readState: QuizState
    private fun persistedState(): QuizState {

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            val stateString = getString(prefState, "")

            readState = if (stateString.isEmpty()) {
                QuizState()
            } else {
                gson.fromJson(stateString, QuizState::class.java)
            }
        }
        Timber.v("read state is \n \n \n $readState")
        return readState
    }
}
