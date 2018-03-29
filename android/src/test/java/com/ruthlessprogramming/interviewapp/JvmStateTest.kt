package com.ruthlessprogramming.interviewapp

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ruthlessprogramming.interviewapp.features.quiz.state.QuizReducer
import zendesk.suas.Store
import zendesk.suas.Suas

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader


abstract class JvmStateTest {
    val ASSET_BASE_PATH = "../android/src/main/assets/"
    val jsonFileName = "questions.json"

    val store: Store = createTestStore()
    val gson = Gson()

    private fun createTestStore(): Store {
        return Suas.createStore(
            QuizReducer()
        ).build()
    }

    fun getQuestionMap(): Map<String, List<String>> {
        val type = object : TypeToken<Map<String, List<String>>>() {}.type
        val questionsMap: Map<String, List<String>> = gson.fromJson(readJsonFile(), type)
        return questionsMap
    }

    @Throws(IOException::class)
    fun readJsonFile(filename: String = jsonFileName): String {
        val br = BufferedReader(InputStreamReader(FileInputStream(ASSET_BASE_PATH + filename)))
        val sb = StringBuilder()
        var line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }

        return sb.toString()
    }
}